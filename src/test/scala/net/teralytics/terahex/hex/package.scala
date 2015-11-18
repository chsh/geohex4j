package net.teralytics.terahex

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.geo._

import scala.language.implicitConversions

package object hex {

  private[hex] val latLon2Hex = shearY(Degrees(30))
  private[hex] val hex2LatLon = shearY(Degrees(-30))

  case class Coordinate(v: Vector = Vector()) extends AnyVal

  implicit class CoordinateOps(val c: Coordinate) {

    def toLatLon: LatLon = hex2LatLon(c)
  }

  implicit class LatLonOps(val ll: LatLon) extends AnyVal {

    def toHex: Coordinate = Coordinate(latLon2Hex(ll))
  }

  implicit def coordinateIsVector(c: Coordinate): Vector = c.v
}
