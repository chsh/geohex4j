package net.teralytics.geohex

import scala.math._

object GeoHex {
  private[this] val h_base = 20037508.34
  private[this] val h_deg = Pi * (30.0 / 180.0)
  private[this] val h_k = tan(h_deg)

  def calcHexSize (level: Int): Double = h_base / pow(3.0, level + 1)

  def loc2xy(lon: Double, lat: Double): XY = {
    val x: Double = lon * h_base / 180.0
    var y: Double = Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0)
    y *= h_base / 180.0
    XY(x, y)
  }

  def xy2loc(x: Double, y: Double): Loc = {
    val lon = (x / h_base) * 180.0
    var lat = (y / h_base) * 180.0
    lat = 180 / Pi * (2.0 * atan(exp(lat * Pi / 180.0)) - Pi / 2.0)
    new Loc(lat, lon)
  }

  case class Loc(lat: Double = 0, lon: Double = 0) {
    def normalize(): Loc = {
      if (lon > 180) {
        copy(lon = lon - 360)
      } else if (lon < -180) {
        copy(lon = lon + 360)
      } else this
    }
  }

  case class XY(x: Double = 0, y: Double = 0)

  case class Zone(code: String, lat: Double = 0, lon: Double = 0, x: Long = 0, y: Long = 0) {

    val level = code.length - 2

    def this(lat: Double, lon: Double, x: Long, y: Long, code: String) = this(code, lat, lon, x, y)

    def getHexSize: Double = calcHexSize(level + 2)

    def getHexCoords: Array[Loc] = {
      val h_lat: Double = this.lat
      val h_lon: Double = this.lon
      val h_xy: XY = loc2xy(h_lon, h_lat)
      val h_x: Double = h_xy.x
      val h_y: Double = h_xy.y
      val h_deg: Double = Math.tan(Math.PI * (60.0 / 180.0))
      val h_size: Double = this.getHexSize
      val h_top: Double = xy2loc(h_x, h_y + h_deg * h_size).lat
      val h_btm: Double = xy2loc(h_x, h_y - h_deg * h_size).lat
      val h_l: Double = xy2loc(h_x - 2 * h_size, h_y).lon
      val h_r: Double = xy2loc(h_x + 2 * h_size, h_y).lon
      val h_cl: Double = xy2loc(h_x - 1 * h_size, h_y).lon
      val h_cr: Double = xy2loc(h_x + 1 * h_size, h_y).lon
      Array[GeoHex.Loc](new GeoHex.Loc(h_lat, h_l), new GeoHex.Loc(h_top, h_cl), new GeoHex.Loc(h_top, h_cr),
        new GeoHex.Loc(h_lat, h_r), new GeoHex.Loc(h_btm, h_cr), new GeoHex.Loc(h_btm, h_cl))
    }
  }

}