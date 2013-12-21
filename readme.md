#delimited [![Build Status](https://travis-ci.org/rockymadden/delimited.png?branch=master)](http://travis-ci.org/rockymadden/delimited) [![Coverage Status](https://coveralls.io/repos/rockymadden/delimited/badge.png)](https://coveralls.io/r/rockymadden/delimited)
Dead simple CSV IO for Scala. Read, write, validate, line-by-line, all at once, or lazily.

* __Requirements:__ Scala 2.10.x
* __Documentation:__ Scaladoc
* __Issues:__ [Enhancements](https://github.com/rockymadden/delimited/issues?labels=accepted%2Cenhancement&page=1&state=open), [Questions](https://github.com/rockymadden/delimited/issues?labels=accepted%2Cquestion&page=1&state=open), [Bugs](https://github.com/rockymadden/delimited/issues?labels=accepted%2Cbug&page=1&state=open)
* __Versioning:__ [Semantic Versioning v2.0](http://semver.org/)

## Reader Usage
The recommended usage of ```DelimitedReader``` is to do so via the loan pattern. Loaned readers have automatic resource clean up and there is no need to do so manually.

---

__Line by line:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	Iterator.
		continually(reader.readLine()).
		takeWhile(_.isDefined).
		foreach(println)
}
```
The ```readLine``` function returns ```Option[DelimitedLine]```. The end of file is indicated by the return of ```None``` rather than ```Some```. ```DelimitedLine``` is a type alias to ```IndexedSeq[String]``` and is backed by ```Vector[String]```.

---

__All at once:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	reader.
		readAll().
		foreach(_.foreach(println))
}
```
The ```readAll``` function returns ```Option[List[DelimitedLine]]```.

---

__Lazily:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	reader.
		readToStream().
		take(2).
		foreach(println)
}
```
The ```readToStream``` function returns ```Stream[DelimitedLine]```.

---

__With header:__
```scala
DelimitedReader.usingWithHeader("path/to/file.csv") { (reader, header) =>
	reader.readLine() map { line =>
		val field0 = line(header("field0")))
		val field1 = line(header("field1")))
	}
}
```
The header type is ```Map[String, Int]```. It maps field values in the first line to their respective index.

## Transform Usage
Each read and write function accepts zero to many ```StringTransform```s. The ```StringTransforms``` object provides a handful of built-in filters and other useful transforms, which can be functionally composed. Any function meeting the type requirement of ```StringTransform```, which is a type alias to ```(String => String)```, will work.

---

__In this scenario, we only want to deal with ASCII characters:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	reader.readLine(StringTransforms.filterAscii)
}
```

---

__In this scenario, we only want to deal with alphanumeric ASCII characters:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	reader.readLine(
		StringTransforms.filterAscii andThen
		StringTransforms.filterNotAlphaNumeric
	)
}
```

---

__Custom transform:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	reader.readLine((s) => s.toCharArray.filter(c => c == 'a' || c == 'b').mkString)
}
```

---

## Validator Usage
```DelimitedValidator``` exists to read over each line and ensure one or more checks pass. The ```DelimitedChecks``` object provides a handful of built-in checks, mix and match as needed. Any function meeting the type requirement of ```DelimitedCheck```, which is a type alias to ```(DelimitedLine => Int)```, will work.

---

__In this scenario, we want to ensure the number of fields in each line is consistent and that all fields have a length:__
```scala
val reader = DelimitedReader("path/to/file.csv")

DelimitedValidator(reader).validate(
	DelimitedChecks.checkFieldCountConsistent,
	DelimitedChecks.checkFieldsHaveLength
)
```

---

__In this scenario, we want to ensure all field lengths are consistent (e.g. each field throughout a file has 2 characters):__
```scala
val reader = DelimitedReader("path/to/file.csv")

DelimitedValidator(reader).validate(DelimitedChecks.checkFieldsLengthConsistent)
```

---

## License
```
The MIT License (MIT)

Copyright (c) 2013 Rocky Madden (http://rockymadden.com/)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
