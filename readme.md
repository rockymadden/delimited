#delimited [![Build Status](https://travis-ci.org/rockymadden/delimited.png?branch=master)](http://travis-ci.org/rockymadden/delimited)
Simple CSV IO for Scala. Read, write, validate, and transform. Do so line-by-line, all at once, or via streams.

* __Requirements:__ Scala 2.10+
* __Documentation:__ [Scaladoc](http://rockymadden.com/delimited/scaladoc/)
* __Issues:__ [Enhancements](https://github.com/rockymadden/delimited/issues?labels=accepted%2Cenhancement&page=1&state=open), [Questions](https://github.com/rockymadden/delimited/issues?labels=accepted%2Cquestion&page=1&state=open), [Bugs](https://github.com/rockymadden/delimited/issues?labels=accepted%2Cbug&page=1&state=open)
* __Versioning:__ [Semantic Versioning v2.0](http://semver.org/)

## Depending upon
The project is available on the [Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.rockymadden.delimited%22). Adding a dependency to the core sub-project in various build systems (add other sub-projects as needed):


__Simple Build Tool:__
```scala
libraryDependencies += "com.rockymadden.delimited" %% "delimited-core" % "0.1.0"
```

---

__Gradle:__
```groovy
compile 'com.rockymadden.delimited:delimited-core_2.10:0.1.0'
```

---

__Maven:__
```xml
<dependency>
	<groupId>com.rockymadden.delimited</groupId>
	<artifactId>delimited-core_2.10</artifactId>
	<version>0.1.0</version>
</dependency>
```

---

## Reader Usage
The recommended usage of ```DelimitedReader``` is via the loan pattern, which is provided by functions in its companion object (shown below). Loaned readers have automatic resource clean up. Read functions ultimately return ```DelimitedLine```s, which is a type alias to ```IndexedSeq[String]```.

---

__Line-by-line:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	Iterator.continually(reader.readLine()).takeWhile(_.isDefined).foreach(println)
}
```
The ```readLine``` function returns ```Option[DelimitedLine]```. The end of file is indicated by the return of ```None``` rather than ```Some```.

---

__All-at-once:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	reader.readAll().foreach(_.foreach(println))
}
```
The ```readAll``` function returns ```Option[Seq[DelimitedLine]]```.

---

__Via stream:__
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	reader.readToStream().take(2).foreach(println)
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

---

## Writer Usage
The recommended usage of ```DelimitedWriter``` is via the loan pattern, which is provided by functions in its companion object (shown below). Loaned writers have automatic resource clean up.

---

__Line-by-line:__
```scala
DelimitedWriter.using("path/to/file.csv") { writer =>
	val line = Some(Vector("field0", "field1", "field2"))
	writer.writeLine(line)
}
```

---

__All-at-once:__
```scala
DelimitedWriter.using("path/to/file.csv") { writer =>
	val lines = Some(Seq(
		Vector("field0", "field1", "field2"),
		Vector("field0", "field1", "field2")
	))
	writer.writeAll(lines)
}
```

---

__Via stream:__
```scala
DelimitedReader.using("path/to/file.csv") { => reader
	DelimitedWriter.using("path/to/anotherfile.csv") { writer =>
		val lines = reader.readToStream()
		writer.writeFromStream(lines)
	}
}
```

---

## Decorating
It is possible to decorate readers and writers with additional functionality, this is provided by rich wrapping via implicits. Decorations include:

* __withTransform:__ Transform line values after reading and/or before writing. A handful of pre-built transforms are located in the [transform module](https://github.com/rockymadden/delimited/blob/master/core/src/main/scala/com/rockymadden/delimited/Transform.scala).

---

Non-decorated usage:
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	// Do something with reader.
}
```

---

Apply a filter so that we only get alphabetical characters in each line:
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	decoratedReader = reader withTransform StringTransform.filterAlpha

	// Do something with decoratedReader.
}
```

---

Make your own:
```scala
DelimitedReader.using("path/to/file.csv") { reader =>
	customTransform: StringTransform = (s) =>
		s.toCharArray.filter(c => c == 'A' || c == 'C' || c == 'G' || c == 'T').mkString
	decoratedReader = reader withTransform customTransform

	// Do something with decoratedReader.
}
```

---

## Validator Usage
Validators exist to ensure files pass one or more checks. A handful of pre-built checks are located in the [check module](https://github.com/rockymadden/delimited/blob/master/core/src/main/scala/com/rockymadden/delimited/Check.scala).

---

In this scenario, we want to ensure the number of fields in each line is consistent and that all fields have a length:
```scala
val reader = DelimitedReader("path/to/file.csv")

DelimitedValidator(reader).validate(
	DelimitedChecks.checkFieldCountConsistent,
	DelimitedChecks.checkFieldsHaveLength
)
```

---

In this scenario, we want to ensure all field lengths are consistent (e.g. each field has 2 characters):
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
