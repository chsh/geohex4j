package net.teralytics.terahex

package object algebra {

  case class Vector(x1: Double = 0.0, x2: Double = 0.0) {

    def scale(k: Double): Vector = Vector(k * x1, k * x2)

    def +(other: Vector): Vector = Vector(x1 + other.x1, x2 + other.x2)

    def -(other: Vector): Vector = Vector(x1 - other.x1, x2 - other.x2)
  }

  type Transform = Vector => Vector

  case class Degrees(d: Double) extends AnyVal

  def skewCoordinateSystem(alpha: Degrees): Transform = { v =>

    val a = alpha.d.toRadians
    val Vector(x1, x2) = v
    val z1 = x1 / math.cos(a)
    Vector(
      z1,
      x2 - z1 * math.sin(a)
    )
  }

  def unskewCoordinateSystem(alpha: Degrees): Transform = { v =>

    val a = alpha.d.toRadians
    val Vector(x1, x2) = v
    Vector(
      x1 * math.cos(a),
      x2 + x1 * math.sin(a)
    )
  }
}
