package net.teralytics

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.geo.LatLon
import net.teralytics.terahex.hex._

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
        .map(loc => s"${loc.lon.lon} ${loc.lat.lat}")
        .mkString("POLYGON ((", ", ", "))")
    }

    def code[Code](implicit encoding: Encoding[Code]): Code = encoding.encode(z)

    def geometry: Seq[LatLon] = ???
  }
}
