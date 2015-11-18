package net.teralytics.terahex

import net.teralytics.terahex.geo._
import net.teralytics.terahex.hex._

import scala.annotation.tailrec

object TeraHex extends Grid {

  case class CellZone(cells: Seq[Cell], code: String) extends Zone {

    override lazy val level: Int = cells.length - 1

    override lazy val location: LatLon = continuous(cells).toLatLon

    override lazy val size: Double = cellSide(level)

    override def geometry: Seq[LatLon] = ???
  }

  val topLevelCellSide: Double = 360D / 27

  def nestingFactor(level: Int = 0): Double = math.pow(3, -level)

  def cellSide(level: Int = 0) = topLevelCellSide * nestingFactor(level)

  def discrete(x: Coordinate, iterations: Int): Seq[Cell] = {

    @tailrec
    def loop(
      iteration: Int = 0,
      zero: Coordinate = Coordinate(),
      result: List[Cell] = List())
    : List[Cell] =

      if (iteration > iterations) result.reverse
      else {
        val diff = Coordinate(x - zero)
        val cell = Coordinate(diff.scale(1 / cellSide(iteration))).round
        loop(iteration + 1, cell.toFractional, cell :: result)
      }

    loop()
  }

  def continuous(xs: Seq[Cell]): Coordinate = {

    @tailrec
    def loop(
      xs: Seq[Cell],
      result: Coordinate = Coordinate(),
      level: Int = 0)
    : Coordinate = xs match {

      case Nil => result

      case cell :: cells =>
        val shift = cell.toFractional.scale(nestingFactor(level))
        loop(cells, Coordinate(result + shift), level + 1)
    }

    loop(xs)
  }

  val dictionary = ('A' to 'Z') ++ ('a' to 'z')

  def encodeCells(cells: Seq[Cell]): String = {
    val (top :: tail) = cells
    val topCode = Seq(top.col.toInt, top.row.toInt).map(dictionary).mkString

    // TODO: consider left and right outliers
    def encodeSubCell(c: Cell): String = (3 * (c.col.toInt + 1) + (c.row.toInt + 1)).toString

    (topCode :: tail.map(encodeSubCell)).mkString
  }

  def decodeCells(code: String): Seq[Cell] = {

    val (topCode, tail) = code.splitAt(2)
    val Array(topCol, topRow) = topCode.map(dictionary.indexOf).toArray
    val top = Cell(Col(topCol), Row(topRow))

    def decodeSubCell(ch: Char): Cell = ???

    top :: tail.toList.map(decodeSubCell)
  }

  override def zoneByLocation(geo: LatLon, level: Int = 0): Zone = {
    val cells = discrete(geo.toHex, level + 1)
    val code = encodeCells(cells)
    CellZone(cells, code)
  }

  override def zoneByCode(code: String): Zone = CellZone(decodeCells(code), code)
}
