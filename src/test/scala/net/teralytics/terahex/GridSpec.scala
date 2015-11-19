package net.teralytics.terahex

import net.teralytics.terahex.Generators._
import net.teralytics.terahex.TeraHex.CellZone
import net.teralytics.terahex.geo._
import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

trait GridSpec extends FlatSpec with PropertyChecks with Matchers {

  def grid: Grid

  "Grid" should "have a well-known zero point" in {
    val zero = LatLon(Lon(0), Lat(0))
    val zone = grid.zoneByLocation(zero, 0)
    zone.code should be("AA")
    zone.location should be(zero)
  }

  it should "produce two-letter code at level 0" in forAll(latlons) { x =>
    grid.encode(x, 0) should fullyMatch regex topCodeRegex
  }

  it should "produce an alphanumeric code at any level" in forAll(latlons, levels) {
    (ll, lev) =>
      val code = grid.encode(ll, lev)
      code should have length (lev + 2)
      code should fullyMatch regex codeRegex
  }

  it should "produce only valid sub cells" in forAll(latlons, levels) {
    (ll, lev) =>
      val CellZone(cells, _) = grid.zoneByLocation(ll, 2)
      cells.tail.foreach { cell =>
        validSubCells should contain(cell)
      }
  }
}

class TeraHexSpec extends GridSpec {

  override val grid = TeraHex

  "Outliers rearrengement" should "eliminate outliers" in forAll(outliers, outliers) {
    (sub, top) =>

      val (_ :: top2 :: sub2 :: Nil) = grid.rearrangeOutliers(List(sub, top, Cell()))
      validSubCells should contain(sub2)
      validSubCells should contain(top2)
  }
}
