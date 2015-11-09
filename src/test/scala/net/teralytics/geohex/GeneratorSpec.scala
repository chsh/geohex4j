package net.teralytics.geohex

import org.scalatest.prop.PropertyChecks
import org.scalatest.{ FlatSpec, Matchers }

class GeneratorSpec extends FlatSpec with PropertyChecks with Matchers {

  import GeoHexGen._

  "Generators" should "generate level up to the max" in forAll(level) {
    l => l should be <= allLevels.max
  }

  it should "generate alphachar top codes" in forAll(topCodes) { code =>
    code should fullyMatch regex topCodeRegex
  }

  it should "generate numeric subcodes" in forAll(subCode(15)) { code =>
    code should fullyMatch regex subCodeRegex
  }

  it should "generate valid codes" in forAll(code) { code =>
    code should fullyMatch regex codeRegex
  }
}
