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
  lazy val location: LatLon = LatLon(grid.inverse(cells).toPoint)

  /**
    * The hexagon inner radius in decimal lat/lon degrees.
    */
  def innerRadius: Double = grid.innerRadius(level)

  /**
    * `LatLon` coordinates of the hexagon corners.
    */
  def geometry: Seq[LatLon] = {

    val center = location.toPoint
    val east = Point(size, 0d)
    Iterator.iterate(east)(_.rotate(60d.toRadians))
      .take(6)
      .map(_ + center)
      .map(LatLon.apply)
      .toSeq
  }

  def moveN: Zone = move(_.moveN)

  def moveS: Zone = move(_.moveS)

  def moveNE: Zone = move(_.moveNE)

  def moveSE: Zone = move(_.moveSE)

  def moveNW: Zone = move(_.moveNW)

  def moveSW: Zone = move(_.moveSW)

  private[this] def move(cellMove: Cell => Cell): Zone =
    if (cells.isEmpty) this
    else copy(cells = cells.init :+ cellMove(cells.last))
}

object Zone {

  def apply(location: LatLon, level: Int)(implicit grid: Grid): Zone = {
    val cells = grid.tessellate(location.toPoint.toHex, level)
    Zone(grid.rootSize, cells)
  }

  def apply[Code](code: Code)(implicit encoding: Encoding[Code]): Zone = encoding.decode(code)

  def zonesWithin(boundingBox: (LatLon, LatLon), level: Int)(implicit grid: Grid): Stream[Zone] = {

    val (from, LatLon(Lon(toLon), Lat(toLat))) = boundingBox
    val start = Zone(from, level)

    val eastPattern: Seq[Zone => Zone] = Seq(_.moveNE, _.moveSE)
    def towardsEast(start: Zone) = Stream
      .continually(eastPattern).flatten
      .scanLeft(start)((z, move) => move(z))
      .takeWhile(_.location.lon.lon < toLon)

    val towardsNorth = Stream
      .continually((z: Zone) => z.moveN)
      .scanLeft(start)((z, move) => move(z))
      .takeWhile(_.location.lat.lat < toLat)

    for {
      sn <- towardsNorth
      we <- towardsEast(sn)
    } yield we
  }
}
