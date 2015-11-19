package net.teralytics

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.hex._


package object terahex {

  implicit class CoordinateOps(val coord: Coordinate) {

    def round: Cell = Cell(Col(coord.v.x1.round), Row(coord.v.x2.round))
  }

  implicit class CellOps(val c: Cell) extends AnyVal {

    def toCoordinate: Coordinate = Coordinate(Vector(c.col.col.toDouble, c.row.row.toDouble))
  }

  implicit class ZoneOps(val z: Zone) extends AnyVal {

    def toWellKnownText: String = z.geometry
      .map(loc => s"${loc.lon.lon} ${loc.lat.lat}")
      .mkString("POLYGON ((", ", ", "))")
  }

}
