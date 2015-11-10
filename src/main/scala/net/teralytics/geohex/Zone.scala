package net.teralytics.geohex

import scala.math._

case class Loc(lat: Double = 0, lon: Double = 0) {
  def normalize(): Loc = {
    val l =
      if (lon > 180) lon - 360
      else if (lon < -180) lon + 360
      else lon
    if (lon != l) copy(lon = l) else this
  }
}

case class XY(x: Double = 0, y: Double = 0)

case class Zone(code: String, lat: Double = 0, lon: Double = 0, x: Long = 0, y: Long = 0) {

  def this(lat: Double, lon: Double, x: Long, y: Long, code: String) = this(code, lat, lon, x, y)

  val level: Int = code.length - 2

  val size: Double = calcHexSize(level)

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
