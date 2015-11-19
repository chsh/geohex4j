package net.teralytics.terahex

package   object algebra {

  case class Distance(d: Double) extends AnyVal

  case class Vector(x1: Double = 0.0, x2: Double = 0.0) {

    def scale(k: Double): Vector = Vector(k * x1, k * x2)

    def +(other: Vector): Vector = Vector(x1 + other.x1, x2 + other.x2)

    def -(other: Vector): Vector = Vector(x1 - other.x1, x2 - other.x2)
  }

  case class Matrix(
    a11: Double, a12: Double,
    a21: Double, a22: Double)

  def mult(m: Matrix, v: Vector): Vector =
    Vector(
      m.a11 * v.x1 + m.a12 * v.x2,
      m.a21 * v.x1 + m.a22 * v.x2)

  type Transform = Vector => Vector

  case class Degrees(d: Double) extends AnyVal {

    def shearDistance: Distance = Distance(math.tan(math.toRadians(d)))
  }

  def shearY(deg: Degrees): Transform = {

    val d = deg.shearDistance

    val m = Matrix(
      1.0, 0.0,
      d.d, 1.0)

    mult(m, _)
  }
}
