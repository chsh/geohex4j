package net.teralytics.terahex

import com.vividsolutions.jts.geom.{ Point => GeoPoint, MultiPolygon, Polygon, GeometryFactory, Coordinate }

object GeoConversions {

  implicit class LatLonGeoOps(val loc: LatLon) extends AnyVal {

    def toCoordinate: Coordinate = new Coordinate(loc.lon.lon, loc.lat.lat)

    def toGeometry(implicit gf: GeometryFactory): GeoPoint = gf.createPoint(toCoordinate)
  }

  implicit class ZoneGeoOps(val zone: Zone) extends AnyVal {

    def toGeometry(implicit gf: GeometryFactory): Polygon = {
      val coords = zone.geometry.map(_.toCoordinate)
      val closedRing = coords :+ coords.head
      gf.createPolygon(closedRing.toArray)
    }
  }

  implicit class PolygonSeqOps[A <: Polygon](val seq: Seq[A]) extends AnyVal {

    def combine(implicit gf: GeometryFactory): MultiPolygon =
      gf.createMultiPolygon(seq.toArray)
  }

}
