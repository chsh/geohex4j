package net.teralytics.terahex

import net.teralytics.terahex.hex._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{ FlatSpec, Matchers }

class GridSpec extends FlatSpec with PropertyChecks with Matchers {

  import Generators._

  "Grid" should "produce no cells at level 0" in forAll(geoGrids, latlons) { (grid, loc) =>
    grid.discrete(loc.toHex, 0) should have length 0
  }

  it should "produce only valid sub cells" in forAll(geoGrids, latlons, levels) {
    (grid, ll, lev) =>
      val cells = grid.discrete(ll.toHex, lev)
      cells.foreach(validSubCells should contain(_))
  }

  it should "be reversible up to a certain precision" in forAll(coordinates, levels) {
    (coord, level) =>

      val grid = bigEnoughGrid(coord)
      val cells = grid.discrete(coord, 2)
      val result = grid.continuous(cells)
      result.v.x2 should be (coord.v.x2 +- (grid.size(level) / 2))
  }
}
