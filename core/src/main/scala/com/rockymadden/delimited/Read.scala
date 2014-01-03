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


	sealed abstract class Reader[A](protected val br: BufferedReader) extends Closeable {
		protected[delimited] val read: (BufferedReader => Option[String]) = (br) => {
			val line = br.readLine()
			if (line == null) None else Some(line)
		}

		override def close(): Unit = if (br != null) br.close()

		def readToStream(transforms: StringTransform*): Stream[A] =
			Stream.continually(readLine(transforms: _*)).takeWhile(_.isDefined).map(_.get)

		def readAll(transforms: StringTransform*): Option[Seq[A]] = {
			val buffer = new ListBuffer[A]()

			Iterator.continually(readLine(transforms: _*)).takeWhile(_.isDefined).foreach{ buffer ++= _ }

			if (!buffer.isEmpty) Some(buffer.result()) else None
		}

		def readLine(transforms: StringTransform*): Option[A]
	}


	final case class DelimitedReader(
		bufferedReader: BufferedReader,
		private val delimiter: Char = ','
	) extends Reader[DelimitedLine](bufferedReader) {
		private val parser = DelimitedParser(delimiter)

		override def readLine(transforms: StringTransform*): Option[DelimitedLine] =
			(read andThen parser.parse.apply)(br) map { _.map(transformString(_, transforms: _*)) }
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


	final case class TextReader(bufferedReader: BufferedReader) extends Reader[TextLine](bufferedReader) {
		override def readLine(transforms: StringTransform*): Option[TextLine] =
			(read andThen TextParser.parse.apply)(br) map { transformString(_, transforms: _*) }
	}


	object TextReader {
		def using(bufferedReader: BufferedReader)(f: TextReader => Unit): Unit = {
			val reader = apply(bufferedReader)

			try f(reader)
			finally reader.close()
		}
	}
}
