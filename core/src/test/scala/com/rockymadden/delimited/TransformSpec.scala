package com.rockymadden.delimited

import org.specs2.mutable.SpecificationWithJUnit

final class TransformSpec extends SpecificationWithJUnit {
	import Transform._

	"StringTransform filterAlpha()" should {
		"handle String" in {
			StringTransform.filterAlpha("aBc123" + 0x250.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransform filterNotAlpha()" should {
		"handle String" in {
			StringTransform.filterNotAlpha("aBc123" + 0x250.toChar) must beEqualTo("123" + 0x250.toChar)
		}
	}

	"StringTransform filterAlphaNumeric()" should {
		"handle String" in {
			StringTransform.filterAlphaNumeric("aBc123" + 0x250.toChar) must beEqualTo("aBc123")
		}
	}

	"StringTransform filterNotAlphaNumeric()" should {
		"handle String" in {
			StringTransform.filterNotAlphaNumeric("aBc123" + 0x250.toChar) must beEqualTo("" + 0x250.toChar)
		}
	}

	"StringTransform filterAscii()" should {
		"handle String" in {
			StringTransform.filterAscii("aBc" + 0x80.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransform filterNotAscii()" should {
		"handle String" in {
			StringTransform.filterNotAscii("aBc" + 0x100.toChar) must beEqualTo("" + 0x100.toChar)
		}
	}

	"StringTransform filterExtendedAscii()" should {
		"handle String" in {
			StringTransform.filterExtendedAscii("aBc" + 0x100.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransform filterNotExtendedAscii()" should {
		"handle String" in {
			StringTransform.filterNotExtendedAscii("aBc" + 0x250.toChar) must beEqualTo("" + 0x250.toChar)
		}
	}

	"StringTransform filterLatin()" should {
		"handle String" in {
			StringTransform.filterLatin("aBc" + 0x250.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransform filterNotLatin()" should {
		"handle String" in {
			StringTransform.filterNotLatin("aBc" + 0x300.toChar) must beEqualTo("" + 0x300.toChar)
		}
	}

	"StringTransform filterLowerCase()" should {
		"handle String" in {
			StringTransform.filterLowerCase("aBc123" + 0x250.toChar) must beEqualTo("ac")
		}
	}

	"StringTransform filterNotLowerCase()" should {
		"handle String" in {
			StringTransform.filterNotLowerCase("aBc123" + 0x250.toChar) must beEqualTo("B123" + 0x250.toChar)
		}
	}

	"StringTransform filterNumeric()" should {
		"handle String" in {
			StringTransform.filterNumeric("aBc123" + 0x250.toChar) must beEqualTo("123")
		}
	}

	"StringTransform filterNotNumeric()" should {
		"handle String" in {
			StringTransform.filterNotNumeric("aBc123" + 0x250.toChar) must beEqualTo("aBc" + 0x250.toChar)
		}
	}

	"StringTransform filterUpperCase()" should {
		"handle String" in {
			StringTransform.filterUpperCase("aBc123" + 0x250.toChar) must beEqualTo("B")
		}
	}

	"StringTransform filterNotUpperCase()" should {
		"handle String" in {
			StringTransform.filterNotUpperCase("aBc123" + 0x250.toChar) must beEqualTo("ac123" + 0x250.toChar)
		}
	}
}
