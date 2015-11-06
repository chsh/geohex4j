package net.teralytics.geohex

import scala.collection.mutable
import scala.math._

object GeoHex {
  private[this] val h_key = ('A' to 'Z') ++ ('a' to 'z')
  private[this] val h_base = 20037508.34
  private[this] val h_deg = Pi * (30.0 / 180.0)
  private[this] val h_k = tan(h_deg)

  def encode(lat: Double, lon: Double, level: Int): String =
    org.geohex.geohex4j.GeoHex.getZoneByLocation(lat, lon, level).code

  def decode(code: String): Zone = getZoneByCode(code)

  def calcHexSize(level: Int): Double = h_base / pow(3.0, level + 1)

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

  def getZoneByCode(code: String): Zone = {
    val level = code.length
    val h_size = calcHexSize(level)
    val unit_x = 6 * h_size
    val unit_y = 6 * h_size * h_k
    var h_x = 0L
    var h_y = 0L
    var h_dec9 = "" + (h_key.indexOf(code(0)) * 30 + h_key.indexOf(code(1))) + code.substring(2)

    if (isOneOrFive(h_dec9(0))
      && isNotOneTwoOrFive(h_dec9(1))
      && isNotOneTwoOrFive(h_dec9(2))) {

      if (h_dec9(0) == '5') {
        h_dec9 = "7" + h_dec9.substring(1, h_dec9.length)
      } else if (h_dec9(0) == '1') {
        h_dec9 = "3" + h_dec9.substring(1, h_dec9.length)
      }
    }

    var d9xlen = h_dec9.length

    {
      var i = 0
      while (i < level + 1 - d9xlen) {
        {
          h_dec9 = "0" + h_dec9
          d9xlen += 1
          i += 1
        }
      }
    }

    val h_dec3 = new StringBuilder()

    {
      var i = 0
      while (i < d9xlen) {
        val dec9i = h_dec9(i).toString.toInt
        val h_dec0 = Integer.toString(dec9i, 3)
        if (h_dec0.length == 1) {
          h_dec3.append("0")
        }
        h_dec3.append(h_dec0)
        i += 1
      }
    }

    val h_decx = mutable.ArrayBuffer[Char]()
    val h_decy = mutable.ArrayBuffer[Char]()

    {
      var i = 0
      while (i < h_dec3.length / 2) {
        h_decx.append(h_dec3(i * 2))
        h_decy.append(h_dec3(i * 2 + 1))
        i += 1
      }
    }

    {
      var i = 0
      while (i <= level) {
        val h_pow = Math.pow(3, level - i).toLong
        if (h_decx(i) == '0') {
          h_x -= h_pow
        }
        else if (h_decx(i) == '2') {
          h_x += h_pow
        }
        if (h_decy(i) == '0') {
          h_y -= h_pow
        }
        else if (h_decy(i) == '2') {
          h_y += h_pow
        }
        i += 1
      }
    }

    val h_lat_y: Double = (h_k * h_x * unit_x + h_y * unit_y) / 2
    val h_lon_x: Double = (h_lat_y - h_y * unit_y) / h_k
    val h_loc = xy2loc(h_lon_x, h_lat_y).normalize()
    Zone(code, h_loc.lat, h_loc.lon, h_x, h_y)
  }

  private[this] def isOneOrFive(c: Char): Boolean = c == '1' || c == '5'

  private[this] def isNotOneTwoOrFive(c: Char): Boolean =
    c != '1' && c != '2' && c != '5'
}