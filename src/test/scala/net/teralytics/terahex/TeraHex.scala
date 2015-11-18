package net.teralytics.terahex

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

      if (iteration >= iterations) result.reverse
      else {
        val diff = x - zero
        val cell = Coordinate(diff.scale(1.0 / size(iteration))).round
        val zeroDelta = cell.toCoordinate.scale(size(iteration))
        loop(iteration + 1, Coordinate(zero + zeroDelta), cell :: result)
      }

    loop(0, Coordinate(), List())
  }

  def continuous(xs: Seq[Cell]): Coordinate = {

    @tailrec
    def loop(xs: Seq[Cell], result: Coordinate, level: Int): Coordinate = xs match {

      case Nil => result

      case cell :: cells =>
        val shift = cell.toCoordinate.scale(size(level))
        loop(cells, Coordinate(result + shift), level + 1)
    }

    loop(xs, Coordinate(), 0)
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
