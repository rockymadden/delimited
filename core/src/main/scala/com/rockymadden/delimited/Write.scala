package com.rockymadden.delimited

object Write {
	import java.io.{BufferedWriter, Closeable, File, FileOutputStream, OutputStreamWriter}
	import scala.collection.immutable.Seq
	import scala.language.implicitConversions
	import Line._
	import Parse._
	import Transform._


	def toBufferedWriter(file: File): BufferedWriter =
		new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))

	def toBufferedWriter(file: String): BufferedWriter =
		(((f: String) => new File(f)) andThen toBufferedWriter)(file)


	implicit def fileToBufferedWriter(f: File): BufferedWriter = toBufferedWriter(f)
	implicit def stringToBufferedWriter(s: String): BufferedWriter = toBufferedWriter(s)


	sealed trait Writer[A] extends Closeable {
		override def close(): Unit

		def writeFromStream(ls: Stream[A]): Unit

		def writeAll(ls: Option[Seq[A]]): Unit

		def writeLine(l: Option[A]): Unit
	}


	sealed abstract class BasicWriter[A](protected val bw: BufferedWriter) extends Writer[A] {
		override def close(): Unit = if (bw != null) bw.close()

		override def writeFromStream(ls: Stream[A]): Unit = ls.foreach { l => writeLine(Some(l)) }

		override def writeAll(ls: Option[Seq[A]]): Unit = ls map { ls => ls foreach { l => writeLine(Some(l)) } }
	}


	case class DelimitedWriter(
		bufferedWriter: BufferedWriter,
		private val delimiter: Char = ','
	) extends BasicWriter[DelimitedLine](bufferedWriter) {
		private val separator = System.getProperty("line.separator")
		private val parser = DelimitedParser(delimiter)

		override def writeLine(line: Option[DelimitedLine]): Unit = {
			parser.parse.unapply(line) map { s => bufferedWriter.write(s + separator) }
		}
	}

	object DelimitedWriter {
		def using(
			bufferedWriter: BufferedWriter,
			delimiter: Char = ','
		)(f: DelimitedWriter => Unit): Unit = {
			val writer = apply(bufferedWriter, delimiter)

			try f(writer)
			finally writer.close()
		}

		def usingTsv(bufferedWriter: BufferedWriter)(f: DelimitedWriter => Unit): Unit =
			using(bufferedWriter, 0x9.toChar)(f)
	}


	final class DelimitedWriterDecorator(val dw: DelimitedWriter) {
		val withTransform: (StringTransform => DelimitedWriter) = (st) => new DelimitedWriter(null) {
			private val base: DelimitedWriter = dw
			private val transform: StringTransform = st

			override def close(): Unit = base.close()

			override def writeFromStream(ls: Stream[DelimitedLine]): Unit =
				base.writeFromStream(ls.map(_.map(transform)))

			override def writeAll(ls: Option[Seq[DelimitedLine]]): Unit =
				base.writeAll(ls.map(_.map(_.map(transform))))

			override def writeLine(l: Option[DelimitedLine]): Unit =
				base.writeLine(l.map(_.map(transform)))
		}
	}


	case class TextWriter(bufferedWriter: BufferedWriter) extends BasicWriter[TextLine](bufferedWriter) {
		private val separator = System.getProperty("line.separator")

		override def writeLine(line: Option[TextLine]): Unit = line map { l =>
			bufferedWriter.write(l + separator)
		}
	}


	object TextWriter {
		def using(bufferedWriter: BufferedWriter)(f: TextWriter => Unit): Unit = {
			val writer = apply(bufferedWriter)

			try f(writer)
			finally writer.close()
		}
	}


	final class TextWriterDecorator(val tw: TextWriter) {
		val withTransform: (StringTransform => TextWriter) = (st) => new TextWriter(null) {
			private val base: TextWriter = tw
			private val transform: StringTransform = st

			override def close(): Unit = base.close()

			override def writeFromStream(ls: Stream[TextLine]): Unit =
				base.writeFromStream(ls.map(transform))

			override def writeAll(ls: Option[Seq[TextLine]]): Unit =
				base.writeAll(ls.map(_.map(transform)))

			override def writeLine(l: Option[TextLine]): Unit =
				base.writeLine(l.map(transform))
		}
	}
}
