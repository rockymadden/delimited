package com.rockymadden.delimited

import org.specs2.mutable.SpecificationWithJUnit

final class ValidateSpec extends SpecificationWithJUnit {
	import Check._
	import Read._
	import Validate._
	import ValidateSpec._

	"DelimitedValidator validate()" should {
		"handle with valid file and single check" in {
			val noneValidator = DelimitedValidator(DelimitedReader(NoneCsv))
			val readerValidator = DelimitedValidator(DelimitedReader(ReaderCsv))

			noneValidator.validate(DelimitedChecks.checkFieldCountConsistent) must beFalse
			readerValidator.validate(DelimitedChecks.checkFieldCountConsistent) must beTrue
		}
		"handle with valid file and multiple checks" in {
			val noneValidator = DelimitedValidator(DelimitedReader(NoneCsv))
			val readerValidator = DelimitedValidator(DelimitedReader(ReaderCsv))

			noneValidator.validate(
				DelimitedChecks.checkFieldCountConsistent,
				DelimitedChecks.checkFieldsLengthConsistent
			) must beFalse
			readerValidator.validate(
				DelimitedChecks.checkFieldCountConsistent,
				DelimitedChecks.checkFieldsHaveLength,
				DelimitedChecks.checkFieldsLengthConsistent
			) must beTrue
		}
	}

	"TextValidator validate()" should {
		"handle with valid file and single check" in {
			val noneValidator = TextValidator(TextReader(NoneCsv))
			val readerValidator = TextValidator(TextReader(ReaderCsv))

			noneValidator.validate(TextChecks.checkLengthConsistent) must beFalse
			readerValidator.validate(TextChecks.checkLengthConsistent) must beTrue
		}
		"handle with valid file and multiple checks" in {
			val noneValidator = TextValidator(TextReader(NoneCsv))
			val readerValidator = TextValidator(TextReader(ReaderCsv))

			noneValidator.validate(
				TextChecks.checkHaveLength,
				TextChecks.checkLengthConsistent
			) must beFalse
			readerValidator.validate(
				TextChecks.checkHaveLength,
				TextChecks.checkLengthConsistent
			) must beTrue
		}
	}
}

object ValidateSpec {
	private final val ReaderCsv = "core/target/scala-2.10/test-classes/com/rockymadden/delimited/Reader.csv"
	private final val NoneCsv = "core/target/scala-2.10/test-classes/com/rockymadden/delimited/None.csv"
}
