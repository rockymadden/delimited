package com.rockymadden.delimited

object Read {
	import java.io.{BufferedReader, Closeable, File, InputStreamReader, FileInputStream}
	import scala.collection.mutable.ListBuffer
	import scala.collection.immutable.Seq
	import scala.language.implicitConversions
	import Line._
	import Parse._
	import Transform._


	def toBufferedReader(file: File): BufferedReader =
		new BufferedReader(new InputStreamReader(new FileInputStream(file)))

	def toBufferedReader(file: String): BufferedReader =
		(((f: String) => new File(f)) andThen toBufferedReader)(file)


	implicit def fileToBufferedReader(f: File): BufferedReader = toBufferedReader(f)
	implicit def stringToBufferedReader(s: String): BufferedReader = toBufferedReader(s)


	sealed trait Reader[A] extends Closeable {
		override def close(): Unit

		def readToStream(): Stream[A]

		def readAll(): Option[Seq[A]]

		def readLine(): Option[A]
	}


	sealed abstract class BasicReader[A](protected val br: BufferedReader) extends Reader[A] {
		protected[delimited] val read: (BufferedReader => Option[String]) = (br) => {
			val line = br.readLine()
			if (line == null) None else Some(line)
		}

		override def close(): Unit = if (br != null) br.close()

		override def readToStream(): Stream[A] = Stream.continually(readLine()).takeWhile(_.isDefined).map(_.get)

		override def readAll(): Option[Seq[A]] = {
			val buffer = new ListBuffer[A]()

			Iterator.continually(readLine()).takeWhile(_.isDefined).foreach{ buffer ++= _ }

			if (!buffer.isEmpty) Some(buffer.result()) else None
		}

		def readLine(): Option[A]
	}


	case class DelimitedReader(
		bufferedReader: BufferedReader,
		private val delimiter: Char = ','
	) extends BasicReader[DelimitedLine](bufferedReader) {
		private val parser = DelimitedParser(delimiter)

		override def readLine(): Option[DelimitedLine] = (read andThen parser.parse.apply)(br)
	}


	object DelimitedReader {
		def using(
			bufferedReader: BufferedReader,
			delimiter: Char = ','
		)(f: DelimitedReader => Unit): Unit = {
			val reader = apply(bufferedReader, delimiter)

			try f(reader)
			finally reader.close()
		}

		def usingWithHeader(
			bufferedReader: BufferedReader,
			delimiter: Char = ','
		)(f: (DelimitedReader, Map[String, Int]) => Unit): Unit = {
			val reader = apply(bufferedReader, delimiter)

			try f(reader, reader.readLine().fold(Map.empty[String, Int]) { a =>
				Map(a.zipWithIndex: _*)
			})
			finally reader.close()
		}
	}


	final class DelimitedReaderDecorator(val dr: DelimitedReader) {
		val withTransform: (StringTransform => DelimitedReader) = (st) => new DelimitedReader(null) {
			private val base: DelimitedReader = dr
			private val transform: StringTransform = st

			override def close(): Unit = base.close()

			override def readToStream(): Stream[DelimitedLine] = base.readToStream().map(_.map(transform))

			override def readAll(): Option[Seq[DelimitedLine]] = base.readAll().map(_.map(_.map(transform)))

			override def readLine(): Option[DelimitedLine] = base.readLine().map(_.map(transform))
		}
	}


	case class TextReader(bufferedReader: BufferedReader) extends BasicReader[TextLine](bufferedReader) {
		override def readLine(): Option[TextLine] = (read andThen TextParser.parse.apply)(br)
	}


	object TextReader {
		def using(bufferedReader: BufferedReader)(f: TextReader => Unit): Unit = {
			val reader = apply(bufferedReader)

			try f(reader)
			finally reader.close()
		}
	}

	final class TextReaderDecorator(val tr: TextReader) {
		val withTransform: (StringTransform => TextReader) = (st) => new TextReader(null) {
			private val base: TextReader = tr
			private val transform: StringTransform = st

			override def close(): Unit = base.close()

			override def readToStream(): Stream[TextLine] = base.readToStream().map(transform)

			override def readAll(): Option[Seq[TextLine]] = base.readAll().map(_.map(transform))

			override def readLine(): Option[TextLine] = base.readLine().map(transform)
		}
	}
}
