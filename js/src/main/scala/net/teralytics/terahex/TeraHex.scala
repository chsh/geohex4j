package net.teralytics.terahex

import scala.scalajs.js
import js.annotation.{ JSName, JSExport, JSExportAll }
import js.JSConverters._

@JSExport("terahex")
@JSExportAll
object TeraHex {

  private[this] implicit val grid: Grid = Grid(300)
  private[this] implicit val encoding: Encoding[String] = StringEncoding

  def encode(lon: Double, lat: Double, level: Int): String = zoneByLocation(lon, lat, level).code

  def decode(code: String): ZoneJs = new ZoneJs(Zone(code))

  def level(code: String): Int = encoding.level(code)

  def zoneByLocation(lon: Double, lat: Double, level: Int): ZoneJs = new ZoneJs(Zone(LatLon(Lon(lon), Lat(lat)), level))

  def size(level: Int): Double = grid.size(level)

  def zonesWithin(fromLon: Double, fromLat: Double, toLon: Double, toLat: Double, level: Int): js.Array[ZoneJs] =
    Zone.zonesWithin(LatLon(Lon(fromLon), Lat(fromLat)) -> LatLon(Lon(toLon), Lat(toLat)), level)
      .map(new ZoneJs(_))
      .toJSArray
}

object StringEncoding extends Encoding[String] {

  override def encode(zone: Zone): String = Encoding.numeric.encode(zone).toString()

  override def decode(code: String): Zone = Encoding.numeric.decode(BigInt(code))

  override def level(code: String): Int = Encoding.numeric.level(BigInt(code))
}

@JSExportAll
@JSName("LatLon")
case class LatLonJs(lon: Double, lat: Double)

object LatLonJs {

  def apply(loc: LatLon): LatLonJs = LatLonJs(loc.lon.lon, loc.lat.lat)
}

@JSExportAll
@JSName("Zone")
case class ZoneJs(z: Zone) {

  val level: Int = z.level

  val size: Double = z.size

  def location: LatLonJs = LatLonJs(z.location)

  def innerRadius: Double = z.innerRadius

  def geometry: js.Array[LatLonJs] = z.geometry.map(LatLonJs.apply).toJSArray

  def wellKnownText: String = z.toWellKnownText

  def code: String = StringEncoding.encode(z)
}
