package net.teralytics.geohex

import org.scalatest.{ FunSuite, Matchers }
import spray.json._

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

    readJsonResource("/test-files/testdata_ll2hex.json").foreach {
      case Vector(JsNumber(lat), JsNumber(lon), JsNumber(level), JsString(expected)) =>
        val actual = encode(lat.toDouble, lon.toDouble, level.toInt)
        assert(actual == expected)
    }
  }

  test("convert GeoHex to coordinates") {

    readJsonResource("/test-files/testdata_hex2ll.json").foreach {
      case Vector(JsString(code), JsNumber(lat), JsNumber(lon)) =>
        val zone = decode(code)
        assertDouble(lat.toDouble, zone.lat)
        assertDouble(lon.toDouble, zone.lon)
    }
  }

  test("convert coordinates to GeoHex polygon") {

    readJsonResource("/test-files/testdata_ll2polygon.json").foreach {
      case Vector(JsNumber(lat), JsNumber(lon), JsNumber(level), poly@_*) =>
        val zone = getZoneByLocation(lat.toDouble, lon.toDouble, level.toInt)
        val expected_polygon = poly
          .map(_.asInstanceOf[JsNumber])
          .map(_.value.toDouble)
          .grouped(2)
          .map { case Seq(lat2, lon2) => new Loc(lat2, lon2) }
        assertPolygon(expected_polygon.toSeq, zone.getHexCoords)
    }
  }

  test("convert coordinates to GeoHex size") {

    readJsonResource("/test-files/testdata_ll2hexsize.json").foreach {
      case Vector(JsNumber(lat), JsNumber(lon), JsNumber(level), JsNumber(expected)) =>
        val zone = getZoneByLocation(lat.toDouble, lon.toDouble, level.toInt)
        assertDouble(expected.toDouble, zone.size)
    }
  }

  def readJsonResource(path: String): Seq[Vector[JsValue]] = {
    val rows = JsonParser(readResource(path)) match {
      case JsArray(xs) => xs
      case _ => throw new Exception("JSON file should contain an array")
    }
    rows.map {
      case JsArray(els) => els
      case _ => throw new Exception("Each row should be an array")
    }
  }

  private def readResource(path: String): String = {
    io.Source.fromInputStream(getClass.getResourceAsStream(path)).mkString
  }

  private def assertDouble(expected: Double, actual: Double) {
    assert(actual === expected +- 1e-12)
  }

  private def assertPolygon(expected: Iterable[Loc], actual: Iterable[Loc]) {
    expected.zip(actual).foreach {
      case (expectedLoc, actualLoc) =>
        assertDouble(expectedLoc.lat, actualLoc.lat)
        assertDouble(expectedLoc.lon, actualLoc.lon)
    }
  }
}