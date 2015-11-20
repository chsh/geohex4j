package net.teralytics.terahex

import net.teralytics.terahex.algebra._
import net.teralytics.terahex.hex._

import scala.annotation.tailrec

case class Grid(rootSize: Double) {

  def nestingFactor(level: Int = 0): Double = math.pow(3, -level)

  def size(level: Int = 0) = rootSize * nestingFactor(level)

  def continuous(xs: Seq[Cell]): Coordinate = {

    val x = xs.zipWithIndex
      .map { case (cell, level) => cell.toCoordinate.scale(size(level)) }
      .foldLeft(Vector())(_ + _)

    Coordinate(x)
  }

  def discrete(x: Coordinate, iterations: Int): Seq[Cell] = {

    @tailrec
    def loop(iteration: Int, zero: Coordinate, result: List[Cell]): List[Cell] =

      if (iteration >= iterations) Outliers.restructure(result)
      else {
        val diff = x - zero
        val cell = Coordinate(diff.scale(1.0 / size(iteration))).round
        val zeroDelta = cell.toCoordinate.scale(size(iteration))
        loop(iteration + 1, Coordinate(zero + zeroDelta), cell :: result)
      }

    loop(0, Coordinate(), List())
  }
}

object Grid {

}
