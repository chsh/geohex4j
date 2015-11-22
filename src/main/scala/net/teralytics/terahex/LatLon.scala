package net.teralytics.terahex

import scala.math._

case class Lat(lat: Double = 0) extends AnyVal

case class Lon(lon: Double = 0) extends AnyVal

case class LatLon(lon: Lon = Lon(), lat: Lat = Lat()) {

  private val lonRange = (-180d, 180d)
  private val latRange = (-90d, 90d)

  def normalized = {
    LatLon(
      Lon(wraparound(lon.lon, lonRange)),
      Lat(wraparound(lat.lat, latRange)))
  }

  def distance(other: LatLon): Double = {
    val a = wraparound(other.lon.lon - lon.lon, lonRange)
    val b = wraparound(other.lat.lat - lat.lat, latRange)
    sqrt(a * a + b * b)
  }

  /**
    * Wrap `value` in a `range`.
    * @param range (min, max] range of values.
    */
  private[this] def wraparound(value: Double, range: (Double, Double)): Double = {
    val (min, max) = range
    val d = max - min
    val norm =
      if (value < min)
        max - (min - value) % d
      else
        (value - min) % d + min
    val res = if (norm == min) max else norm
    assert(res > min, s"$res should be > $min")
    assert(res <= max, s"$res should be <= $max")
    res
  }
}
