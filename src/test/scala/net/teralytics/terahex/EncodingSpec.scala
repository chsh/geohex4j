package net.teralytics.terahex

import org.scalacheck.Gen
import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

class EncodingSpec extends FlatSpec with PropertyChecks with Matchers {

  val topLevelCells = for {
    c <- Gen.choose(-26, 25)
    r <- Gen.choose(-26, 25)
  } yield Cell(Col(c), Row(r))

  val validSubCells = Seq(
    (-2, 1),
    (-1, 0),
    (-1, 1),
    (0, -1),
    (0, 0),
    (0, 1),
    (1, -1),
    (1, 0),
    (2, -1))
  .map { case (c, r) => Cell(Col(c), Row(r)) }

  val subCells = Gen.oneOf(validSubCells)

  "Terahex encoding" should "roundtrip top level cells" in forAll(topLevelCells) { cell =>

      val code = Encoding.topLevel.encode(cell)
      Encoding.topLevel.decode(code) should be(cell)
  }

  it should "roundtrip sub-cells" in forAll(subCells) { cell =>

    val code = Encoding.subCell.encode(cell)
    Encoding.subCell.decode(code) should be(cell)
  }
}
