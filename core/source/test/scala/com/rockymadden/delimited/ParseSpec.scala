package com.rockymadden.delimited

import org.specs2.mutable.SpecificationWithJUnit

final class ParseSpec extends SpecificationWithJUnit {
	import Parse._

	"DelimitedParser parse apply()" should {
		"handle None" in {
			val line = None
			val parser = DelimitedParser(',')

			parser.parse(line) match {
				case Some(s) => failure
				case None => success
			}
		}
		"handle empty lines" in {
			val line = Some("")
			val parser = DelimitedParser(',')

			parser.parse(line) match {
				case Some(s) =>
					s must beEqualTo(IndexedSeq.empty)
					success
				case None => failure
			}
		}
		"handle simple lines" in {
			val line = Some("one, two, three")
			val parser = DelimitedParser(',')

			parser.parse(line) match {
				case Some(s) =>
					s must beEqualTo(IndexedSeq("one", "two", "three"))
					success
				case None => failure
			}
		}
		"handle complex lines" in {
			val line = Some("\"one\", two, \"three, four\"")
			val parser = DelimitedParser(',')

			parser.parse(line) match {
				case Some(s) =>
					s must beEqualTo(IndexedSeq("one", "two", "three, four"))
					success
				case None => failure
			}
		}
	}

	"DelimitedParser parse unapply()" should {
		"handle None" in {
			val line = None
			val parser = DelimitedParser(',')

			line match {
				case parser.parse(s) => failure
				case _ => success
			}
		}
		"handle empty lines" in {
			val line = Some(IndexedSeq(""))
			val parser = DelimitedParser(',')

			line match {
				case parser.parse(s) =>
					s must beEqualTo("")
					success
				case _ => failure
			}
		}
		"handle simple lines" in {
			val line = Some(IndexedSeq("one", "two", "three"))
			val parser = DelimitedParser(',')

			line match {
				case parser.parse(s) =>
					s must beEqualTo("one, two, three")
					success
				case _ => failure
			}
		}
		"handle complex lines" in {
			val line = Some(IndexedSeq("one", "two", "three, four"))
			val parser = DelimitedParser(',')

			line match {
				case parser.parse(s) =>
					s must beEqualTo("one, two, \"three, four\"")
					success
				case _ => failure
			}
		}
	}

	"TextParser parse apply()" should {
		"handle None" in {
			val line = None

			TextParser.parse(line) match {
				case Some(s) => failure
				case None => success
			}
		}
		"handle empty lines" in {
			val line = Some("")

			TextParser.parse(line) match {
				case Some(s) =>
					s must beEqualTo("")
					success
				case None => failure
			}
		}
		"handle non-empty lines" in {
			val line = Some("one, two, three")

			TextParser.parse(line) match {
				case Some(s) =>
					s must beEqualTo("one, two, three")
					success
				case None => failure
			}
		}
	}

	"TextParser parse unapply()" should {
		"handle None" in {
			val line = None

			line match {
				case TextParser.parse(s) => failure
				case _ => success
			}
		}
		"handle empty lines" in {
			val line = Some("")

			line match {
				case TextParser.parse(s) =>
					s must beEqualTo("")
					success
				case _ => failure
			}
		}
		"handle non-empty lines" in {
			val line = Some("one, two, three")

			line match {
				case TextParser.parse(s) =>
					s must beEqualTo("one, two, three")
					success
				case _ => failure
			}
		}
	}
}
