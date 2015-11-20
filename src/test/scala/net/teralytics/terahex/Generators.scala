package net.teralytics.terahex

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.geo._
import net.teralytics.terahex.hex._
import org.scalacheck.Gen._

import scala.math._

object Generators {

  val vectors = for {
    x1 <- chooseNum(-1000d, 1000d)
    x2 <- chooseNum(-1000d, 1000d)
  } yield Vector(x1, x2)

  val coordinates = vectors.map(Coordinate)

  def bigEnoughGrid(x: Coordinate) = Grid(max(max(abs(x.v.x1), abs(x.v.x2)), 1d) * 3)

  val latlons = for {
    lon <- chooseNum(-179.999999, 180d, 0d)
    lat <- chooseNum(-89.999999, 90d, 0d)
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
