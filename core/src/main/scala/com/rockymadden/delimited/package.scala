package com.rockymadden

package object delimited {
	import scala.language.implicitConversions
	import Read._
	import Write._

	implicit def delimitedReaderToDecoratedDelimitedReader(dr: DelimitedReader): DelimitedReaderDecorator =
		new DelimitedReaderDecorator(dr)
	implicit def delimitedWriterToDecoratedDelimitedWriter(dw: DelimitedWriter): DelimitedWriterDecorator =
		new DelimitedWriterDecorator(dw)
	implicit def textReaderToDecoratedTextReader(tr: TextReader): TextReaderDecorator =
		new TextReaderDecorator(tr)
	implicit def textWriterToDecoratedTextWriter(tw: TextWriter): TextWriterDecorator =
		new TextWriterDecorator(tw)
}
