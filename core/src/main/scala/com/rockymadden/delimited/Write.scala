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


	sealed abstract class Writer[A](protected val bw: BufferedWriter) extends Closeable {
		override def close(): Unit = if (bw != null) bw.close()

		def writeFromStream(lines: Stream[A], transforms: StringTransform*): Unit =
			lines.foreach { l => writeLine(Some(l), transforms: _*) }

		def writeAll(lines: Option[Seq[A]], transforms: StringTransform*): Unit =
			lines map { ls => ls foreach { l => writeLine(Some(l), transforms: _*) } }

		def writeLine(line: Option[A], transforms: StringTransform*): Unit
	}


	final case class DelimitedWriter(
		bufferedWriter: BufferedWriter,
		private val delimiter: Char = ','
	) extends Writer[DelimitedLine](bufferedWriter) {
		private val parser = DelimitedParser(delimiter)

		override def writeLine(line: Option[DelimitedLine], transforms: StringTransform*): Unit = {
			parser.parse.unapply(line map { seq =>
				seq map { transformString(_, transforms: _*) }
			}) map { s => bufferedWriter.write(s + System.getProperty("line.separator")) }
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


	final case class TextWriter(bufferedWriter: BufferedWriter) extends Writer[TextLine](bufferedWriter) {
		override def writeLine(line: Option[TextLine], transforms: StringTransform*): Unit = line map { l =>
			bufferedWriter.write(transformString(l, transforms: _*) + System.getProperty("line.separator"))
		}
	}


	object TextWriter {
		def using(bufferedWriter: BufferedWriter)(f: TextWriter => Unit): Unit = {
			val writer = apply(bufferedWriter)

			try f(writer)
			finally writer.close()
		}
	}
}
