package com.rockymadden.delimited

object Parse {
	import scala.collection.immutable.VectorBuilder
	import scala.collection.mutable.StringBuilder
	import Line._


	sealed trait Parser


	sealed trait Extractor[A] {
		def apply(line: Option[String]): Option[A]

		def unapply(line: Option[A]): Option[String]
	}


	final case class DelimitedParser(private val delimiter: Char) extends Parser {
		object parse extends Extractor[DelimitedLine] {
			private final val split: ((String, Char) => Vector[String]) = (string, delimiter) => {
				val vectorBuilder = new VectorBuilder[String]()
				val stringBuilder = new StringBuilder()
				val charArray = string.toCharArray
				var quote = false

				(0 until charArray.length) foreach { i =>
					charArray(i) match {
						case `delimiter` =>
							if (quote) stringBuilder += charArray(i)
							else if (!stringBuilder.isEmpty) {
								vectorBuilder += stringBuilder.mkString
								stringBuilder.clear()
							}
						case '"' => {
							// Toggle if not in a quote, if in a quote and this is the last character, or the next
							// character is a delimiter.
							if (!quote || ((i + 1) == charArray.length || charArray(i + 1) == delimiter)) quote = !quote

							stringBuilder += charArray(i)
						}
						case _ => stringBuilder += charArray(i)
					}
				}

				if (!stringBuilder.isEmpty) vectorBuilder += stringBuilder.mkString

				vectorBuilder.result()
			}

			override def apply(line: Option[String]): Option[DelimitedLine] = { line map { l =>
				if (l.length > 0) split(l, delimiter) map { s =>
					if (s.length > 0) {
						val ts = s.trim()
						if (ts.head == '"') ts.slice(1, ts.length - 1) else ts
					} else s
				} else IndexedSeq.empty
			}}

			override def unapply(line: Option[DelimitedLine]): Option[String] = line map { l =>
				val ds = delimiter.toString
				l map { s => if (s.contains("\"") || s.contains(ds)) "\"" + s + "\"" else s } mkString(ds + " ")
			}
		}
	}


	case object TextParser extends Parser {
		object parse extends Extractor[TextLine] {
			override def apply(line: Option[String]): Option[TextLine] = line

			override def unapply(line: Option[TextLine]): Option[String] = line
		}
	}
}
