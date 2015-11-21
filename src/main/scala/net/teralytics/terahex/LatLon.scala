package net.teralytics.terahex


case class Lat(lat: Double = 0) extends AnyVal

case class Lon(lon: Double = 0) extends AnyVal

case class LatLon(lon: Lon = Lon(), lat: Lat = Lat()) {

  def normalized = LatLon(
    Lon(wraparound(lon.lon, (-180, 180))),
    Lat(wraparound(lat.lat, (-90, 90))))

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
