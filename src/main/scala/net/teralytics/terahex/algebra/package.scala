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

  def rotate(alpha: Degrees): Transform = { v =>

    val a = alpha.d.toRadians
    val Vector(x1, x2) = v
    Vector(
      x1 * math.cos(a) - x2 * math.sin(a),
      x1 * math.sin(a) + x2 * math.cos(a))
  }

  /**
    * Wrap `value` in a `range`.
    * @param range (min, max] range of values.
    */
  def wrap(value: Double, range: (Double, Double)): Double = {
    val (min, max) = range
    val d = max - min
    val norm =
      if (value < min)
        max - (min - value) % d
      else
        (value - min) % d + min
    val res = if (norm == min) max else norm
    assert(res > min, s"$res should be > $min")
    assert(res <= max, s"$res should be <= $max")
    res
  }
}
