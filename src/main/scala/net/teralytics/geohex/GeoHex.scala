package net.teralytics.geohex

import scala.math._

object GeoHex {
  private[this] val h_key = ('A' to 'Z') ++ ('a' to 'z')
  private[this] val h_base = 20037508.34
  private[this] val h_k = tan(toRadians(30.0))

  def encode(lat: Double, lon: Double, level: Int): String =
    getZoneByLocation(lat, lon, level).code

  def decode(code: String): Zone = getZoneByCode(code)

  def calcHexSize(level: Int): Double = h_base / pow(3.0, level + 3)

  def loc2xy(lon: Double, lat: Double): XY = {
    val x = lon * h_base / 180.0
    val y = h_base * log(tan((90.0 + lat) * Pi / 360.0)) / Pi
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

  def getZoneByLocation(lat: Double, lon: Double, level: Int): Zone =
    org.geohex.geohex4j.GeoHex.getZoneByLocation(lat, lon, level)

  def getZoneByCode(code: String): Zone = {

    val length = code.length
    var dec9 = toNumeric(code)
    var d9xlen = dec9.length

    {
      var i = 0
      while (i < length + 1 - d9xlen) {
        {
          dec9 = "0" + dec9
          d9xlen += 1
          i += 1
        }
      }
    }

    val h_dec3 = new StringBuilder()

    {
      var i = 0
      while (i < d9xlen) {
        val dec9i = dec9(i).toString.toInt
        val h_dec0 = Integer.toString(dec9i, 3)
        if (h_dec0.length == 1) {
          h_dec3.append("0")
        }
        h_dec3.append(h_dec0)
        i += 1
      }
    }

    val decXY = h_dec3.toArray.grouped(2)
      .map { case Array(x, y) => (x, y) }
      .toIndexedSeq

    val (h_x, h_y) = decXY.zipWithIndex
      .foldLeft((0L,0L)) {
        case ((xAcc, yAcc), ((xChar, yChar), i)) =>
          val h_pow = pow(3, length - i).toLong
          val x =
            if (xChar == '0') xAcc - h_pow
            else if (xChar == '2') xAcc + h_pow
            else xAcc
          val y =
            if (yChar == '0') yAcc - h_pow
            else if (yChar == '2') yAcc + h_pow
            else yAcc
          (x, y)
      }

    val h_size = calcHexSize(length - 2)
    val unit_x = 6 * h_size
    val unit_y = 6 * h_size * h_k
    val h_lat_y = (h_k * h_x * unit_x + h_y * unit_y) / 2
    val h_lon_x = (h_lat_y - h_y * unit_y) / h_k
    val h_loc = xy2loc(h_lon_x, h_lat_y).normalize()
    Zone(code, h_loc.lat, h_loc.lon, h_x, h_y)
  }

  private[this] def toNumeric(code: String): String = {
    val alpha = h_key.indexOf(code(0)) * 30 + h_key.indexOf(code(1))
    val dec9 = alpha.toString + code.substring(2)
    val replace = """([15])([^125])(\d*)""".r
    dec9 match {
      case replace(a, _*) if a == "5" => "7" + dec9.tail
      case replace(a, _*) if a == "1" => "3" + dec9.tail
      case _ => dec9
    }
  }
}