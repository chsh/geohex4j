package net.teralytics.terahex

import net.teralytics.terahex.Generators._
import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

class EncodingSpec extends FlatSpec with PropertyChecks with Matchers {

  "Terahex encoding" should "roundtrip top level cells" in forAll(topLevelCells) { cell =>

    val code = Encoding.topLevel.encode(cell)
    Encoding.topLevel.decode(code) should be(cell)
  }

  it should "roundtrip sub-cells" in forAll(subCells) { cell =>

    val code = Encoding.subCell.encode(cell)
    Encoding.subCell.decode(code) should be(cell)
  }
}
