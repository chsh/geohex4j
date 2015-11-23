package net.teralytics.geohex

object GeoHex {

  def encode(lat: Double, lon: Double, level: Int): String =
    getZoneByLocation(lat, lon, level).code

  def decode(code: String): Zone = getZoneByCode(code)

  def getZonesWithin(boundingBox: BoundingBox, level: Int): Seq[Zone] = {
    val ((fromLat, fromLon), (toLat, toLon)) = boundingBox
    val step = circumradiusInDegrees(level) / 2
    val zones = for {
      lat <- fromLat to toLat by step
      lon <- fromLon to toLon by step
    } yield getZoneByLocation(lat, lon, level)
    zones.distinct
  }

  def getZoneByCode(code: String): Zone = {

    val (h_x: Long, h_y: Long) = Encoding.decode(code)
    val level = code.length - 2
    val h_size = circumradiusInMetersAtEquator(level)
    val unit_x = 6 * h_size
    val unit_y = 6 * h_size * h_k
    val h_lat_y = (h_k * h_x * unit_x + h_y * unit_y) / 2
    val h_lon_x = (h_lat_y - h_y * unit_y) / h_k
    val h_loc = xy2loc(h_lon_x, h_lat_y)
      .normalize()
    Zone(code, h_loc.lat, h_loc.lon, h_x, h_y)
  }

  def getZoneByLocation(lat: Double, lon: Double, level: Int): Zone = {
    var xy = getCellByLocation(lat, lon, level)
    val unit = unitSize(level)
    val h_lat: Double = (h_k * xy.x * unit.x + xy.y * unit.y) / 2
    val h_lon: Double = (h_lat - xy.y * unit.y) / h_k
    val coord: XY = XY(h_lon, h_lat)
    val size = circumradiusInMetersAtEquator(level)
    if (halfEquatorInMeters - coord.x < size) {
      xy = xy.swap
    }
    var z_loc = xy2loc(coord.x, coord.y)
    if (halfEquatorInMeters - h_lon < size) {
      z_loc = Loc(z_loc.lat, 180)
    }
    val code: String = Encoding.encode(xy.x, xy.y, level)
    Zone(code, z_loc.lat, z_loc.lon, xy.x, xy.y)
  }
}