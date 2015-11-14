package net.teralytics

package object terahex {

  case class LatLon(lat: Double, lon: Double) {

    def toHex = HexFractional.tupled(latLon2Hex(lat -> lon))
  }

  case class HexFractional(x: Double, y: Double) {

    def toLatLon = LatLon.tupled(hex2LatLon(x -> y))
  }

  case class Hex(col: Long, row: Long)

  type Vector = (Double, Double)
  type Matrix = (Vector, Vector)

  def mult(m: Matrix, v: Vector): Vector = {
    val (
      (a11, a12),
      (a21, a22)
      ) = m
    val (x1, x2) = v

    (
      a11 * x1 + a12 * x2,
      a21 * x1 + a22 * x2
      )
  }

  type Transform = Vector => Vector

  def shear(degrees: Double): Transform = {
    val b = math.tan(math.toRadians(degrees))

    val m = (
      (1d, 0d),
      (b, 0d))

    mult(m, _)
  }

  val latLon2Hex = shear(30)
  val hex2LatLon = shear(-30)
}
