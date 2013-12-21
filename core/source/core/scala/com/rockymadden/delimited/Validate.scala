package com.rockymadden.delimited

object Validate {
	import scala.language.implicitConversions
	import Check._
	import Line._
	import Read._


	sealed abstract class Validator[A](protected val reader: Reader[A]) {
		@annotation.tailrec
		final protected[delimited] def validateLine(
			scores: scala.collection.mutable.IndexedSeq[Int],
			checks: (A => Int)*
		): Boolean = {
			val line = reader.readLine()
			val zero = scores.contains(0)

			if (line.isEmpty || zero) {
				try reader.close()
				!zero
			} else {
				val l = line.get

				(0 until checks.length) foreach { i =>
					val score = checks(i)(l)
					val previousScore = scores(i)

					if (previousScore == -1) scores.update(i, score)
					else if (score != scores(i)) scores.update(i, 0)
				}

				validateLine(scores, checks: _*)
			}
		}

		def validate(checks: (A => Int)*): Boolean
	}


	final case class DelimitedValidator(delimitedReader: DelimitedReader) extends Validator[DelimitedLine](delimitedReader) {
		override def validate(checks: DelimitedCheck*): Boolean =
			validateLine(scala.collection.mutable.IndexedSeq.fill(checks.length)(-1), checks: _*)
	}


	final case class TextValidator(textReader: TextReader) extends Validator[TextLine](textReader) {
		override def validate(checks: TextCheck*): Boolean =
			validateLine(scala.collection.mutable.IndexedSeq.fill(checks.length)(-1), checks: _*)
	}
}
