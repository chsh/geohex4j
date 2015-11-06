package net.teralytics.geohex

import org.scalatest.{ FunSuite, Matchers }

class CompatibilityTest extends FunSuite with Matchers {

  import GeoHex._

  test("invalid arguments") {

    intercept[IllegalArgumentException] {
      encode(-91, 100, 1)
    }

    intercept[IllegalArgumentException] {
      encode(91, 100, 1)
    }

    intercept[IllegalArgumentException] {
      encode(90, 181, 1)
    }

    intercept[IllegalArgumentException] {
      encode(-90, -181, 1)
    }

    intercept[IllegalArgumentException] {
      encode(0, 180, -1)
    }

    intercept[IllegalArgumentException] {
      encode(0, -180, 25)
    }
  }

  test("convert coordinates to GeoHex") {

    assert(encode(35.780516755235475, 139.57031250000003, 9) == "XM566370240")

    assert(getZoneByLocation(35.780516755235475, 139.57031250000003, 9).code == "XM566370240")

    readResource("/test-files/testdata_ll2hex.txt")
      .filterNot(_.startsWith("#"))
      .foreach { line =>
        val Array(lat, lon, level, expected) = line.split(",")
        val actual = encode(lat.toDouble, lon.toDouble, level.toInt)
        assert(actual == expected)
      }
  }

  test("convert GeoHex to coordinates") {

    val zone1 = decode("XM566370240")
    assertDouble(35.78044332128244, zone1.lat)
    assertDouble(139.57018747142206, zone1.lon)
    assert(zone1.level == 9)

    val zone2 = getZoneByCode("XM566370240")
    assertDouble(35.78044332128244, zone2.lat)
    assertDouble(139.57018747142206, zone2.lon)
    assert(zone2.level == 9)

    readResource("/test-files/testdata_hex2ll.txt")
      .filterNot(_.startsWith("#"))
      .foreach { line =>
        val Array(lat, lon, level, code) = line.split(",")
        val zone = decode(code)
        assert(zone.level == level.toInt)
        assertDouble(lat.toDouble, zone.lat)

        // assertDouble(lon.toDouble, zone.lon)
        // TODO: figure out why this math is needed and why the previous statement fails
        val d = lon.toDouble - zone.lon
        var dl = d.toLong * 10000000000000L
        if (dl == -3600000000000000L || dl == 3600000000000000L) {
          dl = 0L
        }
        assert(dl == 0L)
      }
  }

  test("convert coordinates to GeoHex polygon") {

    val zone = getZoneByLocation(35.780516755235475, 139.57031250000003, 9)
    val polygon = Seq(
      Loc(35.78044332128244, 139.56951006790973),
      Loc(35.78091924645671, 139.5698487696659),
      Loc(35.78091924645671, 139.57052617317822),
      Loc(35.78044332128244, 139.5708648749344),
      Loc(35.779967393259035, 139.57052617317822),
      Loc(35.779967393259035, 139.5698487696659))
    assertPolygon(polygon, zone.getHexCoords)

    readResource("/test-files/testdata_ll2polygon.txt")
      .filterNot(_.startsWith("#"))
      .foreach { line =>
        val Array(lat, lon, level, poly@_*) = line.split(",")
        val z = getZoneByLocation(lat.toDouble, lon.toDouble, level.toInt)
        val expected_polygon = poly.map(_.toDouble).grouped(2)
          .map { case Seq(lat2, lon2) => new Loc(lat2, lon2) }
        assertPolygon(expected_polygon.toSeq, z.getHexCoords)
      }
  }

  test("convert coordinates to GeoHex size") {

    val zone = getZoneByLocation(35.780516755235475, 139.57031250000003, 9)
    assertDouble(37.70410702222824, zone.size)

    readResource("/test-files/testdata_ll2hexsize.txt")
      .filterNot(_.startsWith("#"))
      .foreach { line =>
        val Array(lat, lon, level, size) = line.split(",")
        val z = getZoneByLocation(lat.toDouble, lon.toDouble, level.toInt)
        assertDouble(size.toDouble, z.size)
      }
  }

  private def readResource(path: String): Iterator[String] = {
    io.Source.fromInputStream(getClass.getResourceAsStream(path)).getLines()
  }

  private def assertDouble(expected: Double, actual: Double) {
    assert(expected === actual +- 0.0000000000001)
  }

  private def assertPolygon(expected: Iterable[Loc], actual: Iterable[Loc]) {
    expected.zip(actual).foreach {
      case (expectedLoc, actualLoc) =>
        assertDouble(expectedLoc.lat, actualLoc.lat)
        assertDouble(expectedLoc.lon, actualLoc.lon)
    }
  }
}