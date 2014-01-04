package com.rockymadden.delimited

import org.specs2.mutable.SpecificationWithJUnit

final class WriteSpec extends SpecificationWithJUnit {
	import java.io.{BufferedWriter, File}
	import scala.concurrent._
	import scala.concurrent.duration._
	import Read._
	import Transform._
	import Write._
	import WriteSpec._

	"toBufferedWriter()" should {
		"handle String" in {
			toBufferedWriter(WriterCsv) match {
				case bw: BufferedWriter =>
					bw.close()
					success
				case _ => failure
			}
		}
		"handle File" in {
			toBufferedWriter(new File(WriterCsv)) match {
				case bw: BufferedWriter =>
					bw.close()
					success
				case _ => failure
			}
		}
	}

	"DelimitedWriter writeAll()" should {
		"handle lines" in {
			val reader = DelimitedReader(ReaderCsv)
			val writer = DelimitedWriter(WriterCsv)

			try writer.writeAll(reader.readAll())
			finally {
				reader.close()
				writer.close()
			}

			val writerReader = DelimitedReader(WriterCsv)
			val lines =
				try writerReader.readAll()
				finally writerReader.close()

			lines must beSome
			lines.get.length must beEqualTo(2)
			foreach(lines.get) { _.length must beEqualTo(3) }
		}
	}

	"DelimitedWriter writeFromStream()" should {
		"handle lines" in {
			val reader = DelimitedReader(ReaderCsv)
			val writer = DelimitedWriter(WriterCsv)

			try writer.writeFromStream(reader.readToStream())
			finally {
				reader.close()
				writer.close()
			}

			val writerReader = DelimitedReader(WriterCsv)
			val lines =
				try writerReader.readAll()
				finally writerReader.close()

			lines must beSome
			lines.get.length must beEqualTo(2)
			foreach(lines.get) { _.length must beEqualTo(3) }
		}
	}

	"DelimitedWriter writeLine()" should {
		"handle lines" in {
			val reader = DelimitedReader(ReaderCsv)
			val writer = DelimitedWriter(WriterCsv)

			try writer.writeLine(reader.readLine())
			finally {
				reader.close()
				writer.close()
			}

			val writerReader = DelimitedReader(WriterCsv)

			try foreach(Iterator.continually(writerReader.readLine()).takeWhile(_.isDefined)) { line =>
				line must beSome
				line.get.length must beEqualTo(3)
			} finally writerReader.close()
		}
	}

	"DelimitedWriter using()" should {
		"pass DelimitedWriter" in {
			val p = Promise[Boolean]()

			DelimitedWriter.using(WriterCsv) { writer => p.success(true) }

			Await.result(p.future, Duration(10, SECONDS)) must beTrue
		}
	}

	"DelimitedWriterDecorator withTransform()" should {
		"return decorated DelimitedWriter" in {
			val reader = DelimitedReader(ReaderCsv)
			val writer = DelimitedWriter(DecoratedWriterCsv) withTransform StringTransform.filterAlpha

			try writer.writeLine(reader.readLine())
			finally {
				reader.close()
				writer.close()
			}

			val writerReader = DelimitedReader(DecoratedWriterCsv)

			try foreach(Iterator.continually(writerReader.readLine()).takeWhile(_.isDefined)) { line =>
				line.get.find(_.indexOf("1") >= 0) must beNone
			} finally writerReader.close()
		}
	}

	"TextWriter writeLine()" should {
		"handle lines" in {
			val reader = TextReader(ReaderCsv)
			val writer = TextWriter(WriterCsv)

			try writer.writeLine(reader.readLine())
			finally {
				reader.close()
				writer.close()
			}

			val writerReader = TextReader(WriterCsv)

			try foreach(Iterator.continually(writerReader.readLine()).takeWhile(_.isDefined)) { _ must beSome }
			finally writerReader.close()
		}
	}

	"TextWriter using()" should {
		"pass TextWriter" in {
			val p = Promise[Boolean]()

			TextWriter.using(WriterCsv) { writer => p.success(true) }

			Await.result(p.future, Duration(10, SECONDS)) must beTrue
		}
	}

	"TextWriterDecorator withTransform()" should {
		"return decorated TextWriter" in {
			val reader = TextReader(ReaderCsv)
			val writer = TextWriter(DecoratedWriterCsv) withTransform StringTransform.filterAlpha

			try writer.writeLine(reader.readLine())
			finally {
				reader.close()
				writer.close()
			}

			val writerReader = TextReader(DecoratedWriterCsv)

			try foreach(Iterator.continually(writerReader.readLine()).takeWhile(_.isDefined)) { line =>
				line.get.indexOf("1") must beEqualTo(-1)
			}
			finally writerReader.close()
		}
	}
}

object WriteSpec {
	private val DecoratedWriterCsv = "core/target/scala-2.10/test-classes/com/rockymadden/delimited/DecoratedWriter.csv"
	private val ReaderCsv = "core/target/scala-2.10/test-classes/com/rockymadden/delimited/Reader.csv"
	private val WriterCsv = "core/target/scala-2.10/test-classes/com/rockymadden/delimited/Writer.csv"
}
