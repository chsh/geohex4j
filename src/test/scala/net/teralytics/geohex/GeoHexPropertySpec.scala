package net.teralytics.geohex

import org.scalacheck.Prop
import org.scalatest.prop.{ Checkers, PropertyChecks }
import org.scalatest.{ FlatSpec, Matchers }

class GeoHexPropertySpec extends FlatSpec with PropertyChecks with Matchers with Checkers {

  import GeoHexGen._

  "Geohex encoding" should "produce two-letter code at level 0" in forAll(latitude, longitude) {
    (lat, lon) =>
      GeoHex.encode(lat, lon, 0) should fullyMatch regex topCodeRegex
  }

  it should "produce up to 17 alphanumeric chars at any level" in forAll(latitude, longitude, level) {
    (lat, lon, lev) =>
      GeoHex.encode(lat, lon, lev) should fullyMatch regex codeRegex
  }

  it can "yield inconsistent codes across levels" in check(Prop.exists(location) {
    case (lat, lon) =>
      val hashes = allLevels.map(GeoHex.encode(lat, lon, _))
      val pairs = hashes.sliding(2) map { case Seq(x, y) => (x, y) }
      pairs.exists { case (parent, child) =>
        !child.startsWith(parent)
      }
  })

  it should "have a well-known zero point" in {
    val zone = GeoHex.getZoneByLocation(0.0, 0.0, 0)
    zone should be(Zone("OY", 0.0, 0.0, 0, 0))
  }
}
