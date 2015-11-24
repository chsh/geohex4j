package net.teralytics.terahex

import scala.scalajs.js.annotation.{ JSExportAll, JSExport }

@JSExport
@JSExportAll
object TeraHex {

  private[this] implicit val grid: Grid = Grid(300)
  private[this] implicit val encoding: Encoding[String] = StringEncoding

  def encode(lon: Double, lat: Double, level: Int): String = zoneByLocation(lon, lat, level).code

  def zoneByLocation(lon: Double, lat: Double, level: Int): ZoneJs = new ZoneJs(Zone(LatLon(Lon(lon), Lat(lat)), level))

  def size(level: Int): Double = grid.size(level)
}

object StringEncoding extends Encoding[String] {

  override def encode(zone: Zone): String = Encoding.numeric.encode(zone).toString()

  override def decode(code: String): Zone = Encoding.numeric.decode(BigInt(code))
}

@JSExport("LatLon")
@JSExportAll
case class LatLonJs(lon: Double, lat: Double)

object LatLonJs {

  def apply(loc: LatLon): LatLonJs = LatLonJs(loc.lon.lon, loc.lat.lat)
}

@JSExport("Zone")
@JSExportAll
case class ZoneJs(z: Zone) {

  val level: Int = z.level

  val size: Double = z.size

  def location: LatLonJs = LatLonJs(z.location)

  def innerRadius: Double = z.innerRadius

  def geometry: Seq[LatLonJs] = z.geometry.map(LatLonJs.apply)

  def toWellKnownText: String = z.toWellKnownText

  def code: String = StringEncoding.encode(z)
}
