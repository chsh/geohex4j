package net.teralytics.geohex

import scala.math._

object GeoHex {
  private[this] val h_k = tan(toRadians(30.0))

  def encode(lat: Double, lon: Double, level: Int): String =
    getZoneByLocation(lat, lon, level).code

  def decode(code: String): Zone = getZoneByCode(code)

  def getZoneByLocation(lat: Double, lon: Double, level: Int): Zone =
    org.geohex.geohex4j.GeoHex.getZoneByLocation(lat, lon, level)

  def getZoneByCode(code: String): Zone = {

    val (h_x: Long, h_y: Long) = Encoding.decodeXY(code)
    val level = code.length - 2
    val h_size = calcHexSize(level)
    val unit_x = 6 * h_size
    val unit_y = 6 * h_size * h_k
    val h_lat_y = (h_k * h_x * unit_x + h_y * unit_y) / 2
    val h_lon_x = (h_lat_y - h_y * unit_y) / h_k
    val h_loc = xy2loc(h_lon_x, h_lat_y).normalize()
    Zone(code, h_loc.lat, h_loc.lon, h_x, h_y)
  }
}