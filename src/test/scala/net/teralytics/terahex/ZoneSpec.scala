package net.teralytics.terahex

import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks
import scala.math._

class ZoneSpec extends FlatSpec with PropertyChecks with Matchers {

  import Generators._
  import TeraHex.encoding

  "Zone encoding" should "produce code of length (levels + 4)" in forAll(geoGrids, latlons, levels) {
    (grid, ll, lev) =>
      implicit val g = grid
      val zone = Zone(location = ll, level = lev)
      zone.code.toString should have length (lev + 4)
  }

  it should "represent an example location as expected" in {

    val cellSize = 100d
    implicit val grid = Grid(cellSize * 3)
    val loc = LatLon(Lon(cellSize + 10), Lat(2))

    val zone = Zone(loc, 1)
    zone.size should be(cellSize)
    zone.cells should contain theSameElementsInOrderAs List(Cell.subNE)

    val loc2 = zone.location
    loc2.lon.lon should be(cellSize * 1.5)
    loc2.lat.lat should be(cellSize * cos(30d.toRadians) +- 1e-10)
  }

  it should "roundtrip locations up to the zone size" in forAll(geoGrids, latlons, levels) {
    (grid, loc, lev) =>

      val code = Zone(loc, lev)(grid).code
      val zone = Zone(code)
      zone.location.distance(loc) should be(0d +- zone.size)
  }

  it should "produce zone location within 360x180 range" in forAll(geoGrids, latlons, levels) {
    (grid, loc, lev) =>

      val zoneLocation = Zone(loc, lev)(grid).location
      zoneLocation.lon.lon should (be > -180d and be <= 180d)
      zoneLocation.lat.lat should (be > -90d and be <= 90d)
  }

  "Numeric zone encoding" should "produce Long numbers for geo locations up to 15 level" in forAll(latlons, levels) {
    (loc, lev) =>

      implicit val encoding = Encoding.numeric
      val code = Zone(loc, lev)(TeraHex.grid).code
      code.isValidLong should be(true)
  }
}
