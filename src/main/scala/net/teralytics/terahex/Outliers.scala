package net.teralytics.terahex

import scala.annotation.tailrec

object Outliers {

  @tailrec
  def restructure(reversedCells: List[Cell], result: List[Cell] = List()): List[Cell] = reversedCells match {
    case Nil => result
    case top :: Nil => top :: result
    case sub :: top :: tail =>
      val (sub2, top2) = restructure(sub, top)
      restructure(top2 :: tail, sub2 :: result)
  }

  def restructure(sub: Cell, top: Cell): (Cell, Cell) = sub match {
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
}
