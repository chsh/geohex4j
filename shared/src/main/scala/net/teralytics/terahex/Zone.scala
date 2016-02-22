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
    else {
      val cs = cells.init :+ cellMove(cells.last)
      val loc = copy(cells = cs).location
      Zone(loc, level)(grid)
    }
}

object Zone {

  def apply(location: LatLon, level: Int)(implicit grid: Grid): Zone = {
    val cells = grid.tessellate(location.toPoint.toHex, level)
    Zone(grid.rootSize, cells)
  }

  def apply[Code](code: Code)(implicit encoding: Encoding[Code]): Zone = encoding.decode(code)

  def zonesWithin(boundingBox: (LatLon, LatLon), level: Int)(implicit grid: Grid): Stream[Zone] = {

    val (from, LatLon(Lon(toLon), Lat(toLat))) = LatLon.mercatorBoundingBox(boundingBox)
    val start = Zone(from, level)

    val eastPattern: Seq[Zone => Zone] = Seq(_.moveNE, _.moveSE)

    def notReachedEast(loc: LatLon) = loc.lon.lon - start.size < toLon

    def latWithinBounds(loc: LatLon) =
      loc.lat.lat + start.innerRadius > from.lat.lat &&
      loc.lat.lat - start.innerRadius < toLat

    def towardsEast(start: Zone) = Stream
      .continually(eastPattern).flatten
      .scanLeft(start)((z, move) => move(z))
      .takeWhile(z => notReachedEast(z.location))
      .filter(z => latWithinBounds(z.location))

    val towardsNorth = Stream
      .iterate(start)(_.moveN)
      .takeWhile(_.location.lat.lat - start.innerRadius < toLat)

    for {
      sn <- towardsNorth
      we <- towardsEast(sn)
    } yield we
  }

  def zonesBetween(line: (LatLon, LatLon), level: Int)(implicit grid: Grid): Seq[Zone] = {
    val (from, to) = line
    val size = grid.size(level)
    val a = from.toPoint.toHex.toCell(size)
    val b = to.toPoint.toHex.toCell(size)

    a.pathTo(b)
      .map(_.toHex(size))
      .map(_.toPoint)
      .map(LatLon(_))
      .map(Zone(_, level))
  }
}
