package net.teralytics.terahex

import net.teralytics.terahex.geo._

case class Col(col: Long = 0) extends AnyVal

case class Row(row: Long = 0) extends AnyVal

case class Cell(col: Col = Col(), row: Row = Row())

trait Zone {

  def level: Int

  def code: String

  def location: LatLon

  def size: Double

  def geometry: Seq[LatLon]
}

trait Grid {

  def zoneByLocation(geo: LatLon, level: Int = 0): Zone

  def zoneByCode(code: String): Zone

  def encode(geo: LatLon, level: Int = 0): String = zoneByLocation(geo, level).code

  def decode(code: String): LatLon = zoneByCode(code).location
}
