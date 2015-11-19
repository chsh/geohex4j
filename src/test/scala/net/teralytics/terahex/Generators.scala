package net.teralytics.terahex

import net.teralytics.terahex.geo._
import org.scalacheck.Gen

import org.scalacheck.Gen._

object Generators {

  val latlons = for {
    lon <- chooseNum(-180d, 180d, 0d)
    lat <- chooseNum(-90d, 90d, 0d)
  } yield LatLon(Lon(lon), Lat(lat))

  val topCodeRegex = "[a-zA-Z]{2}"
  val subCodeRegex = "[0-8]{0,15}"
  val codeRegex = s"$topCodeRegex$subCodeRegex"
  val allLevels = 0 to 15
  def levels: Gen[Int] = oneOf(allLevels) :| "level"

  val validSubCells = Seq(
    Cell.subCenter, Cell.subE, Cell.subN, Cell.subNE, Cell.subNW, Cell.subS, Cell.subSE, Cell.subSW, Cell.subW)

  val subCells = oneOf(validSubCells)

  val outliers = Gen.oneOf(Cell.outlierNE, Cell.outlierNW, Cell.outlierSE, Cell.outlierSW)

  val topLevelCells = for {
    c <- Gen.choose(-26, 25)
    r <- Gen.choose(-26, 25)
  } yield Cell(Col(c), Row(r))
}
