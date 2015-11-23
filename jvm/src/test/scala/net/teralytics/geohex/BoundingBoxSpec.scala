package net.teralytics.geohex

import org.scalatest.{ Matchers, FlatSpec }

class BoundingBoxSpec extends FlatSpec with Matchers {


  "GeoHex" should "return zones for a bounding box" in {

    val boundingBox = ((0.0, 0.0), (40.0, 40.0))
    val zones = GeoHex.getZonesWithin(boundingBox, level = 0)
    zones.map(_.code) should contain theSameElementsAs List(
      "OY", "OX", "Oa", "Ob", "Oc", "PV", "PW", "QA", "PZ")
  }

  it should "return zones for a bounding box on a different level" in {

    val boundingBox = ((0.0, 0.0), (10.0, 10.0))
    val zones = GeoHex.getZonesWithin(boundingBox, level = 1)
    zones.map(_.code) should contain theSameElementsAs List(
      "OY4", "OY3", "OY7", "OY8", "Ob2")
  }

  it should "not mistaken lat for lon" in {

    val boundingBox = ((0.0, 0.0), (30.0, 6.0))
    val zones = GeoHex.getZonesWithin(boundingBox, level = 0)
    zones.map(_.code) should contain theSameElementsAs List(
      "OY", "Oc")
  }
}
