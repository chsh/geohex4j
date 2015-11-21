package net.teralytics.terahex


case class Zone(rootSize: Double, cells: Seq[Cell]) {

  private[this] val grid = Grid(rootSize)

  val level: Int = cells.length

  val size: Double = grid.size(level)

  lazy val location: LatLon = grid.continuous(cells).toPoint
}

object Zone {

  def apply(location: LatLon, level: Int)(implicit grid: Grid): Zone = {
    val cells = grid.discrete(location.toHex, level)
    Zone(grid.rootSize, cells)
  }

  def apply[Code](code: Code)(implicit encoding: Encoding[Code]): Zone = encoding.decode(code)
}
