package net.teralytics.terahex

import org.scalatest.prop.PropertyChecks
import org.scalatest.{ FlatSpec, Matchers }

class GridSpec extends FlatSpec with PropertyChecks with Matchers {

  import Generators._

  "Grid" should "produce no cells at level 0" in forAll(grids, hexCoordinates) {
    (grid, hex) =>
      grid.tessellate(hex, 0) should have length 0
  }

  it should "produce only valid sub cells" in
    forAll(grids, levels) { (grid, lev) =>
      forAll(coordinatesWithin(grid.rootSize)) { hex =>
        val cells = grid.tessellate(hex, lev)
        cells.foreach(validSubCells should contain(_))
      }
    }

  it should "be reversible up to cell size precision" in
    forAll(grids, levels) { (grid, level) =>
      forAll(coordinatesWithin(grid.rootSize)) { hex =>

        val grid = bigEnoughGrid(hex)
        val cells = grid.tessellate(hex, level)
        val result = grid.inverse(cells)
        result.row should be(hex.row +- (grid.size(level) + 1e-10))
      }
    }
}
