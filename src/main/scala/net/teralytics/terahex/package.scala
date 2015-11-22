package net.teralytics

import scala.language.implicitConversions
import scala.math._


package object terahex {

  implicit def latLonIsPoint(loc: LatLon): Point = Point(x = loc.lon.lon, y = -loc.lat.lat)

  implicit def pointIsLatLon(p: Point): LatLon = LatLon(Lon(p.x), Lat(-p.y)).normalized

  implicit class ZoneOps(val z: Zone) extends AnyVal {

    def toWellKnownText: String = {
      val points = z.geometry
      (points :+ points.head)
        .map(loc => f"${loc.lon.lon}%f ${loc.lat.lat}%f")
        .mkString("POLYGON ((", ", ", "))")
    }

    def code[Code](implicit encoding: Encoding[Code]): Code = encoding.encode(z)

    def geometry: Seq[LatLon] = {

      val center: Point = z.location
      val east = Point(z.size, 0d)
      Iterator.iterate(east)(_.rotate(60d.toRadians))
        .take(6)
        .map(_ + center)
        .map(pointIsLatLon)
        .toSeq
    }

    def innerRadius: Double = z.size * cos(30d.toRadians)
  }

}
