package net.teralytics

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.hex._
import net.teralytics.terahex.geo._

package object terahex {

  implicit class CoordinateOps(val coord: Coordinate) {

    def round: Cell = Cell(Col(coord.v.x1.round), Row(coord.v.x2.round))
  }

  implicit class CellOps(val c: Cell) extends AnyVal {

    def toCoordinate: Coordinate = Coordinate(Vector(c.col.col.toDouble, c.row.row.toDouble))
  }

  implicit class ZoneOps(val z: Zone) extends AnyVal {

    def toWellKnownText: String = {
      val points = z.geometry
      (points :+ points.head)
        .map(loc => f"${loc.lon.lon}%f ${loc.lat.lat}%f")
        .mkString("POLYGON ((", ", ", "))")
    }

    def code[Code](implicit encoding: Encoding[Code]): Code = encoding.encode(z)

    def outerRadius = z.size / (2 * math.sin(60d.toRadians))

    def geometry: Seq[LatLon] = {

      val center = z.location
      val east = Vector(outerRadius, 0)
      Iterator.iterate(east)(rotate(Degrees(60)))
        .take(6)
        .map(_ + center)
        .map(_.toLatLon)
        .toSeq
    }
  }

}
