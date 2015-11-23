package net.teralytics.terahex

import net.teralytics.terahex.hex._
import org.scalacheck.Gen._

import scala.math._

object Generators {

  val points = for {
    x <- chooseNum(-1000d, 1000d)
    y <- chooseNum(-1000d, 1000d)
  } yield Point(x, y)

  val hexCoordinates = for {
    x1 <- chooseNum(-1000d, 1000d)
    x2 <- chooseNum(-1000d, 1000d)
  } yield Hex(x1, x2)

  def coordinatesWithin(size: Double) = for {
    x1 <- chooseNum(-size / 6, size / 6)
    x2 <- chooseNum(-size / 6, size / 6)
  } yield Hex(x1, x2)

  def bigEnoughGrid(x: Hex) = Grid(max(max(abs(x.col), abs(x.row)), 1d) * 3)

  val latsDefinedForMercator = chooseNum(-85d, 85d, 0d)

  val latlons = for {
    lon <- chooseNum(-180 + 1e-12, 180d, 0d)
    lat <- latsDefinedForMercator
  } yield LatLon(Lon(lon), Lat(lat))

  val allLevels = 0 to 15

  def levels = oneOf(allLevels) :| "level"

  val validSubCells = Seq(
    Cell.subCenter, Cell.subE, Cell.subN, Cell.subNE, Cell.subNW, Cell.subS, Cell.subSE, Cell.subSW, Cell.subW)

  val subCells = oneOf(validSubCells)

  val outliers = oneOf(Cell.outlierNE, Cell.outlierNW, Cell.outlierSE, Cell.outlierSW)

  val rootSizes = chooseNum(1, 999)

  val grids = rootSizes.map(Grid(_))

  val geoGrids = rootSizes.suchThat(_ > TeraHex.minGeoRootSize).map(Grid(_))

  val rootZones = rootSizes.map(Zone(_, List()))

  val zones = for {
    root <- rootZones
    level <- levels
    cells <- listOfN(level, subCells)
  } yield root.copy(cells = cells)
}
