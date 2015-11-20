package net.teralytics.terahex

import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

class ZoneSpec extends FlatSpec with PropertyChecks with Matchers {

  import Generators._
  import TeraHex.encoding

  "Zone encoding" should "produce code of length (levels + 4)" in forAll(geoGrids, latlons, levels) {
    (grid, ll, lev) =>
      implicit val g = grid
      val zone = Zone(location = ll, level = lev)
      zone.code.toString should have length (lev + 4)
  }

  it should "roundtrip locations up to the zone size" in forAll(geoGrids, latlons, levels) {
    (grid, loc, lev) =>

      val code = Zone(loc, lev)(grid).code
      val zone = Zone(code)
      zone.location.lon.lon should be (loc.lon.lon +- zone.size)
      zone.location.lat.lat should be (loc.lat.lat +- zone.size)
  }
}
