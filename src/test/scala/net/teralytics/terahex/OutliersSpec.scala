package net.teralytics.terahex

import org.scalatest.{ Matchers, FlatSpec }
import org.scalatest.prop.PropertyChecks

class OutliersSpec extends FlatSpec with PropertyChecks with Matchers {

  import Generators._

  "Outliers rearrengement" should "eliminate outliers" in forAll(outliers, outliers) {
    (sub, top) =>

      val (_ :: top2 :: sub2 :: Nil) = Outliers.restructure(List(sub, top, Cell()))
      validSubCells should contain(sub2)
      validSubCells should contain(top2)
  }
}
