package net.teralytics.terahex.algebra

import org.scalacheck.Gen
import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

class TransformationSpec extends FlatSpec with PropertyChecks with Matchers {

  val vectors = for {
    x1 <- Gen.chooseNum(-100d, 100d, 0d)
    x2 <- Gen.chooseNum(-100d, 100d, 0d)
  } yield Vector(x1, x2)

  "Skew transformation" should "alter a non-zero vector" in
    forAll(vectors.suchThat(_ != Vector(0d, 0d)).suchThat(_.x1 != 0d)) { x =>

      val y = skewCoordinateSystem(Degrees(30))(x)
      y.x1 should not be (x.x1 +- 1e-12)
      y.x2 should not be (x.x2 +- 1e-12)
    }

  it should "be reversible" in forAll(vectors) { x =>

    val y = skewCoordinateSystem(Degrees(30))(x)
    val z = unskewCoordinateSystem(Degrees(30))(y)
    z.x1 should be(x.x1 +- 1e-12)
    z.x2 should be(x.x2 +- 1e-12)
  }
}
