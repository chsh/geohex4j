package net.teralytics.terahex.hex

import net.teralytics.terahex.Generators._
import net.teralytics.terahex.geo._
import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

class HexSpec extends FlatSpec with PropertyChecks with Matchers {

  "Hex coordinate system" should "alter a non-zero coordinate" in
    forAll(latlons.suchThat(_ != LatLon(Lon(0d), Lat(0d))).suchThat(_.lon.lon != 0d)) { x =>

      val hex = x.toHex
      hex.x1 should not be (x.x1 +- 1e-12)
      hex.x2 should not be (x.x2 +- 1e-12)
    }

  it should "be reversible" in forAll(latlons) { x =>

    val hex = x.toHex
    val z = hex.toLatLon
    z.x1 should be(x.x1 +- 1e-12)
    z.x2 should be(x.x2 +- 1e-12)
  }

  it should "preserve zero LatLon" in {
    LatLon().toHex should be(Coordinate())
  }

  "Every coordinate" should "be represented as a valid LatLon" in forAll(coordinates) { coord =>

    val loc = coord.toLatLon
    loc.lon.lon should (be > -180d and be <= 180d)
    loc.lat.lat should (be > -90d and be <= 90d)
  }
}
