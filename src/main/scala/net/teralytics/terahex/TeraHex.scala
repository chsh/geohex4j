package net.teralytics.terahex

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.geo._
import net.teralytics.terahex.hex._

import scala.annotation.tailrec

object TeraHex extends Grid {

  case class CellZone(cells: Seq[Cell], code: String) extends Zone {

    override lazy val level: Int = cells.length - 1

    override lazy val location: LatLon = continuous(cells).toLatLon

    override lazy val size: Double = TeraHex.size(level)

    override def geometry: Seq[LatLon] = ???
  }

  val topLevelCellSide: Double = 360D / 27
  val innerRadiusToSideRatio: Double = math.cos(math.toRadians(30))

  val topLevelEncoding = Encoding.topLevel
  val subCellEncoding = Encoding.subCell

  def nestingFactor(level: Int = 0): Double = math.pow(3, -level)

  def side(level: Int = 0) = topLevelCellSide * nestingFactor(level)

  def size(level: Int = 0) = 2 * side(level) * innerRadiusToSideRatio

  def discrete(x: Coordinate, iterations: Int): Seq[Cell] = {

    @tailrec
    def loop(iteration: Int, zero: Coordinate, result: List[Cell]): List[Cell] =

      if (iteration >= iterations) rearrangeOutliers(result)
      else {
        val diff = x - zero
        val cell = Coordinate(diff.scale(1.0 / size(iteration))).round
        val zeroDelta = cell.toCoordinate.scale(size(iteration))
        loop(iteration + 1, Coordinate(zero + zeroDelta), cell :: result)
      }

    loop(0, Coordinate(), List())
  }

  @tailrec
  def rearrangeOutliers(reversedCells: List[Cell], result: List[Cell] = List()): List[Cell] = reversedCells match {
    case Nil => result
    case top :: Nil => top :: result
    case sub :: top :: tail =>
      val (sub2, top2) = rearrangeOutliers(sub, top)
      rearrangeOutliers(top2 :: tail, sub2 :: result)
  }

  def rearrangeOutliers(sub: Cell, top: Cell): (Cell, Cell) = sub match {
    case Cell.outlierNE => (Cell.subW, top.moveNE)
    case Cell.outlierSE => (Cell.subW, top.moveSE)
    case Cell.outlierNW => (Cell.subE, top.moveNW)
    case Cell.outlierSW => (Cell.subE, top.moveSW)
    // TODO: figure out how to get rid of those edge cases:
    case Cell(Col(0), Row(2)) => (Cell.subS, top.moveN)
    case Cell(Col(0), Row(-2)) => (Cell.subN, top.moveS)
    case Cell(Col(2), Row(0)) => (Cell.subSW, top.moveNE)
    case Cell(Col(2), Row(-2)) => (Cell.subNW, top.moveSE)
    case Cell(Col(-2), Row(0)) => (Cell.subNE, top.moveSW)
    case Cell(Col(-2), Row(2)) => (Cell.subSE, top.moveNW)
    case Cell(Col(-2), Row(-1)) => (Cell.subSE, top.moveSW)
    case Cell(Col(2), Row(1)) => (Cell.subNW, top.moveNW)
    case Cell(Col(2), Row(-3)) => (Cell.subSW, top.moveSE)
    case Cell(Col(-2), Row(3)) => (Cell.subNE, top.moveNW)
    case _ => (sub, top)
  }

  def continuous(xs: Seq[Cell]): Coordinate = {

    val x = xs.zipWithIndex
      .map { case (cell, level) => cell.toCoordinate.scale(size(level)) }
      .foldLeft(Vector())(_ + _)

    Coordinate(x)
  }

  def encodeCells(cells: Seq[Cell]): String = {
    val (top :: tail) = cells
    (topLevelEncoding.encode(top) :: tail.map(subCellEncoding.encode)).mkString
  }

  def decodeCells(code: String): Seq[Cell] = {
    val (topCode, tail) = code.splitAt(2)
    val subCellCodes = tail.toList.map(_.toString)
    topLevelEncoding.decode(topCode) :: subCellCodes.map(subCellEncoding.decode)
  }

  override def zoneByLocation(geo: LatLon, level: Int = 0): Zone = {
    val cells = discrete(geo.toHex, level + 1)
    val code = encodeCells(cells)
    CellZone(cells, code)
  }

  override def zoneByCode(code: String): Zone = CellZone(decodeCells(code), code)
}
