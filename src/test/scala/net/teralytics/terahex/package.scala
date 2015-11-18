package net.teralytics

import scala.language.implicitConversions

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.hex._


package object terahex {

  implicit def colIsLong(c: Col): Long = c.col

  implicit def rowIsLong(r: Row): Long = r.row

  implicit class CoordinateOps(val coord: Coordinate) {

    def round: Cell = Cell(Col(coord.v.x1.round), Row(coord.v.x2.round))
  }

  implicit class CellOps(val c: Cell) extends AnyVal {

    def toCoordinate: Coordinate = Coordinate(Vector(c.col.toDouble, c.row.toDouble))
  }

  implicit class ZoneOps(val z: Zone) extends AnyVal {

    def toWellKnownText: String = z.geometry
      .map(loc => s"${loc.lon.lon} ${loc.lat.lat}")
      .mkString("POLYGON ((", ", ", "))")
  }

}
