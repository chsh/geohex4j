package net.teralytics.terahex

import scala.language.implicitConversions

import net.teralytics.terahex.algebra._

package object geo {

  case class Lat(lat: Double = 0) extends AnyVal

  case class Lon(lon: Double = 0) extends AnyVal

  case class LatLon(lon: Lon = Lon(), lat: Lat = Lat()) {

    def normalized = LatLon(
      Lon(wrap(lon.lon, (-180, 180))),
      Lat(wrap(lat.lat, (-90, 90))))
  }

  implicit def latLonIsVector(ll: LatLon): Vector = Vector(ll.lon.lon, ll.lat.lat)

  implicit class VectorOps(val v: Vector) extends AnyVal {

    def toLatLon: LatLon = LatLon(Lon(v.x1), Lat(v.x2)).normalized
  }
}
