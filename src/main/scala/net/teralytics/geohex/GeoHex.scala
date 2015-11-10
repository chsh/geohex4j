package net.teralytics.geohex

import scala.math._

object GeoHex {
  private[this] val h_key = ('A' to 'Z') ++ ('a' to 'z')
  private[this] val h_k = tan(toRadians(30.0))

  def encode(lat: Double, lon: Double, level: Int): String =
    getZoneByLocation(lat, lon, level).code

  def decode(code: String): Zone = getZoneByCode(code)

  def getZoneByLocation(lat: Double, lon: Double, level: Int): Zone =
    org.geohex.geohex4j.GeoHex.getZoneByLocation(lat, lon, level)

  def getZoneByCode(code: String): Zone = {

    val dec9 = toNumeric(code)

    val dec3 = dec9
      .map(_.toString.toInt)
      .map(Integer.toString(_, 3))
      .map { x => if (x.length > 1) x else s"0$x" }
      .mkString

    val decXY = dec3.toArray.grouped(2)
      .map { case Array(x, y) => (x, y) }
      .toIndexedSeq

    val length = code.length

    val (h_x, h_y) = decXY.zipWithIndex
      .foldLeft((0L, 0L)) {
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
    val unpadded = dec9 match {
      case replace(a, _*) if a == "5" => "7" + dec9.tail
      case replace(a, _*) if a == "1" => "3" + dec9.tail
      case _ => dec9
    }
    val padding = "0" * (code.length + 1 - unpadded.length)
    padding + unpadded
  }
}