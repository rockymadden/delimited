package com.rockymadden.delimited

import java.io.{BufferedReader, File}
import org.specs2.mutable.SpecificationWithJUnit
import scala.concurrent._
import scala.concurrent.duration._

final class ReadSpec extends SpecificationWithJUnit {
	import Line._
	import Read._
	import ReadSpec._

	"toBufferedReader()" should {
		"handle String" in {
			toBufferedReader(NoneCsv) match {
				case br: BufferedReader => success
				case _ => failure
			}

			toBufferedReader(ReaderCsv) match {
				case br: BufferedReader => success
				case _ => failure
			}
		}
		"handle File" in {
			toBufferedReader(new File(NoneCsv)) match {
				case br: BufferedReader => success
				case _ => failure
			}

			toBufferedReader(new File(ReaderCsv)) match {
				case br: BufferedReader => success
				case _ => failure
			}
		}
	}

	"DelimitedReader readAll()" should {
		"handle empty lines" in {
			val reader = DelimitedReader(NoneCsv)
			val lines = reader.readAll()

			lines must beSome
			lines.get.length must beEqualTo(4)
			foreach(lines.get) { _.length must beBetween(0, 1) }
		}
		"handle non-empty lines" in {
			val reader = DelimitedReader(ReaderCsv)
			val lines = reader.readAll()

			lines must beSome
			lines.get.length must beEqualTo(2)
			foreach(lines.get) { _.length must beEqualTo(3) }
		}
	}

	"DelimitedReader readToStream()" should {
		"handle empty lines" in {
			val reader = DelimitedReader(NoneCsv)
			val lines = reader.readToStream() take 2

			lines.length must beEqualTo(2)
			foreach(lines) { _.length must beBetween(0, 1) }
		}
		"handle non-empty lines" in {
			val reader = DelimitedReader(ReaderCsv)
			val lines = reader.readToStream().head

			lines.length must beEqualTo(3)
		}
	}

	"DelimitedReader readLine()" should {
		"handle empty lines" in {
			val reader = DelimitedReader(NoneCsv)

			foreach(Iterator.continually(reader.readLine()).takeWhile(_.isDefined)) { line =>
				line must beSome
				line.get.length must beBetween(0, 1)
			}
		}
		"handle non-empty lines" in {
			val reader = DelimitedReader(ReaderCsv)

			foreach(Iterator.continually(reader.readLine()).takeWhile(_.isDefined)) { line =>
				line must beSome
				line.get.length must beEqualTo(3)
			}
		}
	}

	"DelimitedReader companion object using()" should {
		"pass DelimitedReader" in {
			val p1 = Promise[DelimitedLine]()
			val p2 = Promise[DelimitedLine]()

			DelimitedReader.using(ReaderCsv) { reader => p1.success(reader.readLine().get) }
			DelimitedReader.using(NoneCsv) { reader => p2.success(reader.readLine().get) }

			Await.result(p1.future, Duration(10, SECONDS)).length must beEqualTo(3)
			Await.result(p2.future, Duration(10, SECONDS)).length must beEqualTo(0)
		}
	}

	"DelimitedReader companion object usingWithHeader()" should {
		"pass DelimitedReader and header map" in {
			val p1 = Promise[Map[String, Int]]()
			val p2 = Promise[DelimitedLine]()
			val p3 = Promise[String]()

			DelimitedReader.usingWithHeader(ReaderCsv) { (reader, header) =>
				val line = reader.readLine().get

				p1.success(header)
				p2.success(line)
				p3.success(line(header("line0field2")))
			}

			Await.result(p1.future, Duration(10, SECONDS)).size must beEqualTo(3)
			Await.result(p2.future, Duration(10, SECONDS)).length must beEqualTo(3)
			Await.result(p3.future, Duration(10, SECONDS)) must beEqualTo("line1field2")
		}
	}

	"TextReader readLine()" should {
		"handle empty lines" in {
			val reader = TextReader(NoneCsv)

			foreach(Iterator.continually(reader.readLine()).takeWhile(_.isDefined)) { _ must beSome }
		}
		"handle non-empty lines" in {
			val reader = TextReader(ReaderCsv)

			foreach(Iterator.continually(reader.readLine()).takeWhile(_.isDefined)) { _ must beSome }
		}
	}

	"TextReader companion object using()" should {
		"pass TextReader" in {
			val p1 = Promise[TextLine]()
			val p2 = Promise[TextLine]()

			TextReader.using(ReaderCsv) { reader => p1.success(reader.readLine().get) }
			TextReader.using(NoneCsv) { reader => p2.success(reader.readLine().get) }

			Await.result(p1.future, Duration(10, SECONDS)).length must beGreaterThan(0)
			Await.result(p2.future, Duration(10, SECONDS)).length must beEqualTo(0)
		}
	}
}

object ReadSpec {
	private final val ReaderCsv = "build/classes/com/rockymadden/delimited/Reader.csv"
	private final val NoneCsv = "build/classes/com/rockymadden/delimited/None.csv"
}
