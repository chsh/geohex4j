package net.teralytics.terahex

import scala.scalajs.js.annotation.{ JSExportAll, JSExport }

@JSExport
@JSExportAll
object TeraHex {

  private[this] implicit val grid: Grid = Grid(300)

  def encode(lon: Double, lat: Double, level: Int): String = "42"

  def zoneByLocation(lon: Double, lat: Double, level: Int): ZoneJs = new ZoneJs(Zone(LatLon(Lon(lon), Lat(lat)), level))
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

  def code: String = ???
}
