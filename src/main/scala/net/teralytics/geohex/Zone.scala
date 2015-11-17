package net.teralytics.geohex

import scala.math._

case class Loc(lat: Double = 0, lon: Double = 0) {

  def normalize(): Loc = Loc(
    mod(lat, (-90, 90)),
    mod(lon, (-180, 180)))

  /**
    * Modulo of the `value` in a `range`.
    * @param range (min, max] range of values.
    */
  private def mod(value: Double, range: (Double, Double)): Double = {
    val (min, max) = range
    val norm = (value - min) % (max - min) + min
    val res = if (norm == min) max else norm
    assert(res > min, s"$res should be > $min")
    assert(res <= max, s"$res should be <= $max")
    res
  }
}

case class XY(x: Double = 0, y: Double = 0) {

  def swap: XY = XY(x = y, y = x)

  def cell: Cell = Cell(x.toLong, y.toLong)

  def floor: Cell = Cell(x.floor.toLong, y.floor.toLong)

  def round: Cell = Cell(x.round, y.round)
}

case class Cell(x: Long = 0, y: Long = 0) {

  def swap: Cell = Cell(x = y, y = x)
}

case class Zone(code: String, lat: Double = 0, lon: Double = 0, x: Long = 0, y: Long = 0) {

  def this(lat: Double, lon: Double, x: Long, y: Long, code: String) = this(code, lat, lon, x, y)

  val level: Int = code.length - 2

  val size: Double = calcHexSize(level)

  def toWellKnownText: String = getHexCoords
    .map(loc => s"${loc.lat} ${loc.lon}")
    .mkString("POLYGON ((", ", ", "))")

  def getHexCoords: Array[Loc] = {
    val xy = loc2xy(lon, lat)
    val deg = tan(Pi / 3)
    val h_top = xy2loc(xy.x, xy.y + deg * size).lat
    val h_btm = xy2loc(xy.x, xy.y - deg * size).lat
    val h_l = xy2loc(xy.x - 2 * size, xy.y).lon
    val h_r = xy2loc(xy.x + 2 * size, xy.y).lon
    val h_cl = xy2loc(xy.x - size, xy.y).lon
    val h_cr = xy2loc(xy.x + size, xy.y).lon
    Array(
      Loc(lat, h_l),
      Loc(h_top, h_cl),
      Loc(h_top, h_cr),
      Loc(lat, h_r),
      Loc(h_btm, h_cr),
      Loc(h_btm, h_cl))
  }
}
