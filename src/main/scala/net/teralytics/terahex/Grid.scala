package net.teralytics.terahex

import scala.annotation.tailrec

/**
  * Tessellation of a hexagon area into a series of nested sub hexagons:
  * |            _ _ _ _ _
  * |           /X /   \X \
  * |          /_ / N   \ _\
  * |         /   \     /   \
  * |     _ _/ NW  \_ _/ NE  \_ _
  * |    /  /\     /   \     /\  \
  * |   /  /W \_ _/ C   \_ _/E \  \
  * |   \  \  /   \     /   \  /  /
  * |    \_ \/ SW  \_ _/ SE  \/_ /
  * |        \     /   \     /
  * |         \_ _/ S   \_ _/
  * |          \X \     /X /
  * |           \_ \_ _/_ /
  *
  * C/N/S/W/E/NE/NW/SE/SW are the sub cells with discrete hexagonal coordinates.
  * X are the outliers and are rearranged as E/W in the neighbored hexagons to assure consistency.
  *
  */
case class Grid(rootSize: Double) {

  /**
    * Size of a grid cell at a given `level`.
    */
  def size(level: Int = 0) = rootSize * math.pow(3, -level)

  /**
    * Inverse discrete grid tessellation `steps` to reconstruct the hexagonal coordinate that the `steps` represent.
    */
  def inverse(steps: Seq[Cell]): Hex = (Cell() +: steps).zipWithIndex
    .map { case (cell, level) => cell.toHex(size(level)) }
    .foldLeft(Hex())(_ + _)

  /**
    * Convert a fractional hexagonal `coordinate` into a series of discrete grid tessellation steps up to a given
    * number of nesting `levels`.
    */
  def tessellate(coordinate: Hex, levels: Int): Seq[Cell] = {

    @tailrec
    def loop(level: Int, zero: Hex, result: List[Cell]): List[Cell] =

      if (level > levels) Outliers.restructure(result)
      else {
        val diff = coordinate - zero
        val cell = diff.toCell(size(level))
        val zeroDelta = cell.toHex(size(level))
        loop(level + 1, zero + zeroDelta, cell :: result)
      }

    loop(1, Hex(), List())
  }
}
