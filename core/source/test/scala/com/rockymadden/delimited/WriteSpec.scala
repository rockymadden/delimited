package com.rockymadden.delimited

import java.io.{BufferedWriter, File}
import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent._
import scala.concurrent.duration._

final class WriteSpec extends SpecificationWithJUnit {
	import Read._
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

	"DelimitedWriter companion object using()" should {
		"pass DelimitedWriter" in {
			val p = Promise[Boolean]()

			DelimitedWriter.using(WriterCsv) { writer => p.success(true) }

			Await.result(p.future, Duration(10, SECONDS)) must beTrue
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

	"TextWriter companion object using()" should {
		"pass TextWriter" in {
			val p = Promise[Boolean]()

			TextWriter.using(WriterCsv) { writer => p.success(true) }

			Await.result(p.future, Duration(10, SECONDS)) must beTrue
		}
	}
}

object WriteSpec {
	private final val ReaderCsv = "build/classes/com/rockymadden/delimited/Reader.csv"
	private final val WriterCsv = "build/classes/com/rockymadden/delimited/Writer.csv"
}
