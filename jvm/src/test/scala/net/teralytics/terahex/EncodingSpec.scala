package net.teralytics.terahex

import net.teralytics.terahex.Generators._
import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

class EncodingSpec extends FlatSpec with PropertyChecks with Matchers {

  import Encoding._

  "Numeric encoding" should "roundtrip root zones" in forAll(rootZones) { zone =>

    val code = numeric.encode(zone)
    numeric.decode(code) should be(zone)
  }

  it should "encode root zones in 4 digits" in forAll(rootZones) { zone =>

    val code = numeric.encode(zone)
    code.toString should fullyMatch regex """[\d]{4}"""
  }

  it should "roundtrip any zone" in forAll(zones) { zone =>

    val code = Encoding.numeric.encode(zone)
    Encoding.numeric.decode(code) should be(zone)
  }
}
