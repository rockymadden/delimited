package com.rockymadden.delimited

object Check {
	import Line._


	type DelimitedCheck = DelimitedLine => Int
	type TextCheck = TextLine => Int


	object DelimitedChecks {
		val checkFieldsHaveLength: DelimitedCheck = (line) =>
			if (line.takeWhile(_.length > 0).length == line.length) 1 else 0

		val checkFieldsLengthConsistent: DelimitedCheck = (line) =>
			if (line.size > 0) {
				val hl= line.head.length
				if (line.takeWhile(_.length == hl).length == line.length) hl else 0
			} else 0

		val checkFieldCountConsistent: DelimitedCheck = (line) => line.size
	}


	object TextChecks {
		val checkHaveLength: TextCheck = (line) => if (line.length > 0) 1 else 0

		val checkLengthConsistent: TextCheck = (line) => line.length
	}
}
