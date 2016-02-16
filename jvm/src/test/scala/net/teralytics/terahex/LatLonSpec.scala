package net.teralytics.terahex

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.prop.PropertyChecks

class LatLonSpec extends FlatSpec with PropertyChecks with Matchers {

  import Generators._

  "Two locations" should "be fitted into Mercator bounding box" in
    forAll(locationsOutsideOfDomain, locationsOutsideOfDomain) { (l1, l2) =>
      val (from, to) = LatLon.mercatorBoundingBox(l1 -> l2)

      from.lat.lat should (be >= -LatLon.maxMercatorLat and be <= to.lat.lat)
      from.lon.lon should (be >= -LatLon.maxLon and be <= to.lon.lon)

      to.lat.lat should (be <= LatLon.maxMercatorLat)
      to.lon.lon should (be <= LatLon.maxLon)
    }
}
