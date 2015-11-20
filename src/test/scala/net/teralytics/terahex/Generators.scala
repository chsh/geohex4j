package net.teralytics.terahex

import net.teralytics.terahex.geo._
import org.scalacheck.{ Arbitrary, Gen }

import org.scalacheck.Gen._

object Generators {

  val latlons = for {
    lon <- chooseNum(-180d, 180d, 0d)
    lat <- chooseNum(-90d, 90d, 0d)
  } yield LatLon(Lon(lon), Lat(lat))

  val allLevels = 0 to 15
  def levels: Gen[Int] = oneOf(allLevels) :| "level"

  val validSubCells = Seq(
    Cell.subCenter, Cell.subE, Cell.subN, Cell.subNE, Cell.subNW, Cell.subS, Cell.subSE, Cell.subSW, Cell.subW)

  val subCells = oneOf(validSubCells)

  val outliers = Gen.oneOf(Cell.outlierNE, Cell.outlierNW, Cell.outlierSE, Cell.outlierSW)

  val rootSizes = Gen.chooseNum(1, 999)

  val grids = rootSizes.map(Grid(_))

  val geoGrids = rootSizes.suchThat(_ > TeraHex.minGeoRootSize).map(Grid(_))

  val rootZones = rootSizes.map(Zone(_, List()))

  val zones = for {
    root <- rootZones
    level <- levels
    cells <- Gen.listOfN(level, subCells)
  } yield root.copy(cells = cells)
}
