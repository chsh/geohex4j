package net.teralytics.terahex

import net.teralytics.terahex.geo._
import net.teralytics.terahex.hex._

case class Zone(rootSize: Double, cells: Seq[Cell]) {

  private[this] val grid = Grid(rootSize)

  lazy val location: LatLon = grid.continuous(cells).toLatLon

  lazy val level: Int = cells.length

  lazy val size: Double = grid.size(level)
}

object Zone {

  def apply(location: LatLon, level: Int)(implicit grid: Grid): Zone = {
    val cells = grid.discrete(location.toHex, level)
    Zone(grid.rootSize, cells)
  }

  def apply[Code](code: Code)(implicit encoding: Encoding[Code]): Zone = {
    encoding.decode(code)
  }
}
