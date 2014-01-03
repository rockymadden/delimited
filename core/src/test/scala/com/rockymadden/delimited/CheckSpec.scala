package com.rockymadden.delimited

import org.specs2.mutable.SpecificationWithJUnit

final class CheckSpec extends SpecificationWithJUnit {
	import Check._

	"DelimitedChecks checkFieldsHaveLength()" should {
		"return Int with valid lines" in {
			DelimitedChecks.checkFieldsHaveLength(IndexedSeq("a", "b", "c")) must beEqualTo(1)
		}
		"return Int with invalid lines" in {
			DelimitedChecks.checkFieldsHaveLength(IndexedSeq("a", "", "c")) must beEqualTo(0)
		}
	}

	"DelimitedChecks checkFieldsLengthConsistent()" should {
		"return Int with valid lines" in {
			DelimitedChecks.checkFieldsLengthConsistent(IndexedSeq("a", "b", "c")) must beEqualTo(1)
			DelimitedChecks.checkFieldsLengthConsistent(IndexedSeq("az", "bz", "cz")) must beEqualTo(2)
		}
		"return Int with invalid lines" in {
			DelimitedChecks.checkFieldsLengthConsistent(IndexedSeq("a", "", "c")) must beEqualTo(0)
			DelimitedChecks.checkFieldsLengthConsistent(IndexedSeq("az", "b", "cz")) must beEqualTo(0)
		}
	}

	"DelimitedChecks checkFieldCountConsistent()" should {
		"return Int" in {
			DelimitedChecks.checkFieldCountConsistent(IndexedSeq.empty) must beEqualTo(0)
			DelimitedChecks.checkFieldCountConsistent(IndexedSeq("a")) must beEqualTo(1)
			DelimitedChecks.checkFieldCountConsistent(IndexedSeq("a", "b")) must beEqualTo(2)
		}
	}

	"TextChecks checkHaveLength()" should {
		"return Int with valid lines" in {
			TextChecks.checkHaveLength("abc") must beEqualTo(1)
		}
		"return Int with invalid lines" in {
			TextChecks.checkHaveLength("") must beEqualTo(0)
		}
	}

	"TextChecks checkLengthConsistent()" should {
		"return Int" in {
			TextChecks.checkLengthConsistent("") must beEqualTo(0)
			TextChecks.checkLengthConsistent("a") must beEqualTo(1)
			TextChecks.checkLengthConsistent("ab") must beEqualTo(2)
		}
	}
}
