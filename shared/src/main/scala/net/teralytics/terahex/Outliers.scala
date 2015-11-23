package net.teralytics.terahex

import scala.annotation.tailrec


object Outliers {

  /**
    * Restructure outliers in a reversed (from smallest to biggest) list of cells pairwise. The result is ordered
    * from biggest to smallest cells.
    */
  @tailrec
  def restructure(reversedCells: List[Cell], result: List[Cell] = List()): List[Cell] = reversedCells match {
    case Nil => result
    case top :: Nil => top :: result
    case sub :: top :: tail =>
      val (sub2, top2) = restructure(sub, top)
      restructure(top2 :: tail, sub2 :: result)
  }

  /**
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
    * Restructure parent-child relationships of outlier sub areas (marked X) so that they become E/W in the neighbored
    * top hexagons to assure nesting consistency.
    *
    */
  def restructure(sub: Cell, top: Cell): (Cell, Cell) = sub match {
    case Cell.outlierNE => (Cell.subW, top.moveNE)
    case Cell.outlierSE => (Cell.subW, top.moveSE)
    case Cell.outlierNW => (Cell.subE, top.moveNW)
    case Cell.outlierSW => (Cell.subE, top.moveSW)

    // TODO: figure out how to get rid of those edge cases:
    case Cell(-2, -1) => (Cell.subNE, top.moveNW)
    case Cell(-2, 0) => (Cell.subSE, top.moveNW)
    case Cell(-2, 2) => (Cell.subNE, top.moveSW)
    case Cell(-2, 3) => (Cell.subSE, top.moveSW)
    case Cell(0, -2) => (Cell.subS, top.moveN)
    case Cell(0, 2) => (Cell.subN, top.moveS)
    case Cell(2, -3) => (Cell.subNW, top.moveNE)
    case Cell(2, -2) => (Cell.subSW, top.moveNE)
    case Cell(2, 0) => (Cell.subNW, top.moveSE)
    case Cell(2, 1) => (Cell.subSW, top.moveSE)

    case _ => (sub, top)
  }
}
