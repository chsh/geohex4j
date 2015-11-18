package net.teralytics.terahex

import scala.language.implicitConversions

import net.teralytics.terahex.algebra._

package object geo {

  case class Lat(lat: Double) extends AnyVal

  case class Lon(lon: Double) extends AnyVal

  case class LatLon(lon: Lon, lat: Lat)

  implicit def latLonIsVector(ll: LatLon): Vector = Vector(ll.lon.lon, ll.lat.lat)

  implicit def vectorIsLatLon(v: Vector): LatLon = LatLon(Lon(v.x1), Lat(v.x2))
}
