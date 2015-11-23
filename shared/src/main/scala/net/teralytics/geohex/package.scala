package net.teralytics

import scala.math._

package object geohex {

  private[geohex] val halfEquatorInMeters = 20037508.34
  private[geohex] val h_k = tan(toRadians(30.0))
  private[geohex] val initialFactor = 27.0
  private[geohex] def factor(level: Int) = initialFactor * pow(3.0, level)

  type BoundingBox = ((Double, Double), (Double, Double))

  def circumradiusInDegrees(level: Int) = 360.0 / factor(level)

  def circumradiusInMetersAtEquator(level: Int): Double = halfEquatorInMeters / factor(level)

  def inradiusInMetersAtEquator(level: Int): Double = {
    sin(toRadians(60)) * circumradiusInMetersAtEquator(level)
  }

  private[geohex] def unitSize(level: Int): XY = {
    val size = circumradiusInMetersAtEquator(level)
    XY(6 * size, 6 * size * h_k)
  }

  private[geohex] def loc2xy(lon: Double, lat: Double): XY = {
    val x = lon * halfEquatorInMeters / 180.0
    val y = halfEquatorInMeters * log(tan((90.0 + lat) * Pi / 360.0)) / Pi
    XY(x, y)
  }

  private[geohex] def xy2loc(x: Double, y: Double): Loc = {
    val lon = (x / halfEquatorInMeters) * 180.0
    var lat = (y / halfEquatorInMeters) * 180.0
    lat = 180 / Pi * (2.0 * atan(exp(lat * Pi / 180.0)) - Pi / 2.0)
    new Loc(lat, lon)
  }

  private[geohex] def getCellByLocation(lat: Double, lon: Double, level: Int): Cell = {

    if (lat < -90 || lat > 90) throw new IllegalArgumentException("latitude must be between -90 and 90")
    if (lon < -180 || lon > 180) throw new IllegalArgumentException("longitude must be between -180 and 180")
    if (level < 0 || level > 15) throw new IllegalArgumentException("level must be between 0 and 15")

    val xy = loc2xy(lon, lat)
    val unit = unitSize(level)
    val pos = XY(
      (xy.x + xy.y / h_k) / unit.x,
      (xy.y - h_k * xy.x) / unit.y)
    val zero = pos.floor
    val q = XY(
      pos.x - zero.x,
      pos.y - zero.y)
    val result = pos.round

    if ((q.y > -q.x + 1)
      && (q.y < 2 * q.x)
      && (q.y > 0.5 * q.x)) {

      zero.copy(zero.x + 1, zero.y + 1)
    }

    else if ((q.y < -q.x + 1)
      && (q.y > (2 * q.x) - 1)
      && (q.y < (0.5 * q.x) + 0.5)) {

      zero
    }

    else result
  }
}
