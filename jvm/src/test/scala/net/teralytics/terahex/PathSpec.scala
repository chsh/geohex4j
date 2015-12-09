package net.teralytics.terahex

import com.vividsolutions.jts.geom.{ GeometryFactory, PrecisionModel }
import net.teralytics.terahex.GeoConversions._
import net.teralytics.terahex.Generators._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{ FlatSpec, Matchers }

class PathSpec extends FlatSpec with PropertyChecks with Matchers with GeometryMatchers {

  implicit val geoFactory = {
    val scale = 1e9
    val wgs84 = 4326
    new GeometryFactory(new PrecisionModel(scale), wgs84)
  }

  "Hexagon path between two location" should "cover start and end" in forAll(geoGrids) { implicit g =>
    forAll(shortGeoLines) { case (start, end) =>
      val hexagons = Zone.zonesBetween(start -> end, level = 9)
        .map(_.toGeometry)
        .combine
      hexagons should cover(start.toGeometry)
      hexagons should cover(end.toGeometry)
    }
  }

  it should "produce distinct tiles" in forAll(geoGrids) { implicit g =>
    forAll(shortGeoLines) { line =>

      val zones = Zone.zonesBetween(line, level = 9)
      zones should be(zones.distinct)
    }
  }

  it should "produce contiguous sequence of tiles" in forAll(geoGrids) { implicit g =>
    forAll(shortGeoLines) { line =>

      val hexagons = Zone.zonesBetween(line, level = 9).map(_.toGeometry)
      hexagons.sliding(2).foreach {
        case Seq(single) =>
        case Seq(left, right) =>
          left.distance(right) should be(0d +- matcherPrecision)
      }
    }
  }

  it should "intersect with padded line" in forAll(geoGrids) { implicit g =>
    forAll(shortGeoLines) { case (start, end) =>

      val level = 9
      val hexagons = Zone.zonesBetween(start -> end, level).map(_.toGeometry)
      val paddedLine = geoFactory
        .createLineString(Array(start.toCoordinate, end.toCoordinate))
        .buffer(g.size(level))

      hexagons.foreach { hex =>
        paddedLine should intersect(hex)
      }
    }
  }

}
