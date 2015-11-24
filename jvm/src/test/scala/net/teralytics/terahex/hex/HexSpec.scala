package net.teralytics.terahex.hex

import net.teralytics.terahex.Generators._
import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks
import net.teralytics.terahex._

class HexSpec extends FlatSpec with PropertyChecks with Matchers {

  "Hex coordinate system" should "alter a non-zero point" in
    forAll(points.suchThat(_ != Point(0, 0)).suchThat(_.x != 0)) { x =>

      val hex = x.toHex
      hex.col should not be (x.x +- 1e-12)
      hex.row should not be (x.y +- 1e-12)
    }

  it should "be reversible" in forAll(points) { x =>

    val hex = x.toHex
    val z = hex.toPoint
    z.x should be(x.x +- 1e-12)
    z.y should be(x.y +- 1e-12)
  }

  it should "preserve zero point" in {
    Point(0, 0).toHex should be(Hex(0, 0))
  }

  "Cell" should "preserve precision up to its size" in forAll(points, rootSizes) {
    (x, size) =>

      val cell = x.toHex.toCell(size)
      val z = cell.toHex(size).toPoint
      z.x should be(x.x +- size)
      z.y should be(x.y +- size)
  }

  "Every hex coordinate" should "be represented as a valid LatLon" in forAll(hexCoordinates) { hex =>

    val loc = LatLon(hex.toPoint)
    loc.lon.lon should (be > -180d and be <= 180d)
    loc.lat.lat should (be > -90d and be <= 90d)
  }
}
