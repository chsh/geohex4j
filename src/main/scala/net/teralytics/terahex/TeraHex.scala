package net.teralytics.terahex


object TeraHex {

  implicit val encoding = Encoding.numeric

  /**
    * Minimal hexagon height to circumscribe a 360x180 rectangle
    */
  lazy val minGeoRootSize = 90d * (4d * math.cos(30d.toRadians) + math.pow(math.cos(30d.toRadians), 2))

  implicit val grid: Grid = Grid(minGeoRootSize)

  def zoneByLocation(loc: LatLon, level: Int): Zone = Zone(loc, level)

  def encode(loc: LatLon, level: Int): Long = Zone(loc, level).code.toLong

  def decode(code: Long): Zone = encoding.decode(code)
}
