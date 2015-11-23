package net.teralytics.terahex

/**
  * Zone represents a geographic area of a hexagonal geometry. Zone is specified by the size of the root hexagon and
  * a series of tessellation steps for that root hexagon.
  */
case class Zone(rootSize: Double, cells: Seq[Cell]) {

  private[this] val grid = Grid(rootSize)

  val level: Int = cells.length

  /**
    * Size of the side of the hexagon, the same as the hexagon outer radius
    */
  val size: Double = grid.size(level)

  /**
    * Geographic location of the hexagon centroid
    */
  lazy val location: LatLon = grid.inverse(cells).toPoint

  /**
    * The hexagon inner radius in decimal lat/lon degrees.
    */
  def innerRadius: Double = grid.innerRadius(level)

  /**
    * `LatLon` coordinates of the hexagon corners.
    */
  def geometry: Seq[LatLon] = {

    val center: Point = location
    val east = Point(size, 0d)
    Iterator.iterate(east)(_.rotate(60d.toRadians))
      .take(6)
      .map(_ + center)
      .map(pointIsLatLon)
      .toSeq
  }
}

object Zone {

  def apply(location: LatLon, level: Int)(implicit grid: Grid): Zone = {
    val cells = grid.tessellate(location.toHex, level)
    Zone(grid.rootSize, cells)
  }

  def apply[Code](code: Code)(implicit encoding: Encoding[Code]): Zone = encoding.decode(code)
}
