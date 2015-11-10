package net.teralytics

import scala.math._

package object geohex {

  private[geohex] val h_base = 20037508.34

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
}
