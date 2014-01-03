package com.rockymadden.delimited

import org.specs2.mutable.SpecificationWithJUnit

final class TransformSpec extends SpecificationWithJUnit {
	import Transform._

	"StringTransforms filterAlpha()" should {
		"handle String" in {
			StringTransforms.filterAlpha("aBc123" + 0x250.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransforms filterNotAlpha()" should {
		"handle String" in {
			StringTransforms.filterNotAlpha("aBc123" + 0x250.toChar) must beEqualTo("123" + 0x250.toChar)
		}
	}

	"StringTransforms filterAlphaNumeric()" should {
		"handle String" in {
			StringTransforms.filterAlphaNumeric("aBc123" + 0x250.toChar) must beEqualTo("aBc123")
		}
	}

	"StringTransforms filterNotAlphaNumeric()" should {
		"handle String" in {
			StringTransforms.filterNotAlphaNumeric("aBc123" + 0x250.toChar) must beEqualTo("" + 0x250.toChar)
		}
	}

	"StringTransforms filterAscii()" should {
		"handle String" in {
			StringTransforms.filterAscii("aBc" + 0x80.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransforms filterNotAscii()" should {
		"handle String" in {
			StringTransforms.filterNotAscii("aBc" + 0x100.toChar) must beEqualTo("" + 0x100.toChar)
		}
	}

	"StringTransforms filterExtendedAscii()" should {
		"handle String" in {
			StringTransforms.filterExtendedAscii("aBc" + 0x100.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransforms filterNotExtendedAscii()" should {
		"handle String" in {
			StringTransforms.filterNotExtendedAscii("aBc" + 0x250.toChar) must beEqualTo("" + 0x250.toChar)
		}
	}

	"StringTransforms filterLatin()" should {
		"handle String" in {
			StringTransforms.filterLatin("aBc" + 0x250.toChar) must beEqualTo("aBc")
		}
	}

	"StringTransforms filterNotLatin()" should {
		"handle String" in {
			StringTransforms.filterNotLatin("aBc" + 0x300.toChar) must beEqualTo("" + 0x300.toChar)
		}
	}

	"StringTransforms filterLowerCase()" should {
		"handle String" in {
			StringTransforms.filterLowerCase("aBc123" + 0x250.toChar) must beEqualTo("ac")
		}
	}

	"StringTransforms filterNotLowerCase()" should {
		"handle String" in {
			StringTransforms.filterNotLowerCase("aBc123" + 0x250.toChar) must beEqualTo("B123" + 0x250.toChar)
		}
	}

	"StringTransforms filterNumeric()" should {
		"handle String" in {
			StringTransforms.filterNumeric("aBc123" + 0x250.toChar) must beEqualTo("123")
		}
	}

	"StringTransforms filterNotNumeric()" should {
		"handle String" in {
			StringTransforms.filterNotNumeric("aBc123" + 0x250.toChar) must beEqualTo("aBc" + 0x250.toChar)
		}
	}

	"StringTransforms filterUpperCase()" should {
		"handle String" in {
			StringTransforms.filterUpperCase("aBc123" + 0x250.toChar) must beEqualTo("B")
		}
	}

	"StringTransforms filterNotUpperCase()" should {
		"handle String" in {
			StringTransforms.filterNotUpperCase("aBc123" + 0x250.toChar) must beEqualTo("ac123" + 0x250.toChar)
		}
	}
}
