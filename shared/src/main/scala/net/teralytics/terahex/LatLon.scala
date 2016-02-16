package net.teralytics.terahex

import scala.math._

case class Lat(lat: Double = 0) extends AnyVal

case class Lon(lon: Double = 0) extends AnyVal

/**
  * Geographic coordinates.
  */
case class LatLon(lon: Lon = Lon(), lat: Lat = Lat()) {

  import LatLon._

  def normalized = LatLon(
    Lon(wraparound(lon.lon, lonRange)),
    Lat(fitInto(lat.lat, latRange)))

  def distance(other: LatLon): Double = {
    val a = wraparound(other.lon.lon - lon.lon, lonRange)
    val b = wraparound(other.lat.lat - lat.lat, latRange)
    sqrt(a * a + b * b)
  }

  def toPoint: Point = Point(x = lon.lon, y = -mercatorLat)

  private[this] def mercatorLat: Double = log(tan(Pi/4 + lat.lat.toRadians/2)).toDegrees
}

object LatLon {

  val maxLon = 180d
  val maxMercatorLat = 85d
  val maxLat = 90d

  val lonRange = (-maxLon, maxLon)
  val latRange = (-maxLat, maxLat)
  val mercatorLatRange = (-maxMercatorLat, maxMercatorLat)

  /**
    * Wrap `value` in a `range`.
    * @param range (min, max] range of values.
    */
  private[LatLon] def wraparound(value: Double, range: (Double, Double)): Double = {
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

  private[LatLon] def fitInto(value: Double, range: (Double, Double)): Double = range match {
    case (min, max) =>
      if (value > max) max
      else if (value < min) min
      else value
  }

  def mercatorBoundingBox(bbox: (LatLon, LatLon)): (LatLon, LatLon) = bbox match {
    case (LatLon(Lon(lon1), Lat(lat1)), LatLon(Lon(lon2), Lat(lat2))) => (
      LatLon(Lon(fitInto(min(lon1, lon2), lonRange)), Lat(fitInto(min(lat1, lat2), mercatorLatRange))),
      LatLon(Lon(fitInto(max(lon1, lon2), lonRange)), Lat(fitInto(max(lat1, lat2), mercatorLatRange))))
  }

  def apply(p: Point): LatLon = LatLon(Lon(p.x), Lat(inverseMercatorLat(-p.y))).normalized

  private[this] def inverseMercatorLat(lat: Double): Double = atan(sinh(lat.toRadians)).toDegrees
}
