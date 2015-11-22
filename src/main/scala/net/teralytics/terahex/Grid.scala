package net.teralytics.terahex

import scala.annotation.tailrec


case class Grid(rootSize: Double) {

  def nestingFactor(level: Int = 0): Double = math.pow(3, -level)

  def size(level: Int = 0) = rootSize * nestingFactor(level)

  def continuous(xs: Seq[Cell]): Hex = (Cell() +: xs).zipWithIndex
    .map { case (cell, level) => cell.toHex(size(level)) }
    .foldLeft(Hex())(_ + _)

  def discrete(x: Hex, levels: Int): Seq[Cell] = {

    @tailrec
    def loop(level: Int, zero: Hex, result: List[Cell]): List[Cell] =

      if (level > levels) Outliers.restructure(result)
      else {
        val diff = x - zero
        val cell = diff.toCell(size(level))
        val zeroDelta = cell.toHex(size(level))
        loop(level + 1, zero + zeroDelta, cell :: result)
      }

    loop(1, Hex(), List())
  }
}
