package net.teralytics

import scala.annotation.tailrec
import scala.language.implicitConversions

package object terahex {

  object algebra {

    case class Distance(d: Double) extends AnyVal

    case class Vector(x1: Double = 0.0, x2: Double = 0.0) {

      def scale(k: Double): Vector = Vector(k * x1, k * x2)

      def +(other: Vector): Vector = Vector(x1 + other.x1, x2 + other.x2)

      def -(other: Vector): Vector = Vector(x1 - other.x1, x2 - other.x2)
    }

    case class Matrix(
      a11: Double, a12: Double,
      a21: Double, a22: Double)

    def mult(m: Matrix, v: Vector): Vector =
      Vector(
        m.a11 * v.x1 + m.a12 * v.x2,
        m.a21 * v.x1 + m.a22 * v.x2)

    type Transform = Vector => Vector

    case class Degrees(d: Double) extends AnyVal {

      def shearDistance: Distance = Distance(math.tan(math.toRadians(d)))
    }

    def shearY(deg: Degrees): Transform = {

      val d = deg.shearDistance

      val m = Matrix(
        1.0, 0.0,
        d.d, 0.0)

      mult(m, _)
    }
  }

  object geo {

    import algebra._

    case class Lat(lat: Double) extends AnyVal

    case class Lon(lon: Double) extends AnyVal

    case class LatLon(lon: Lon, lat: Lat)

    implicit def latLonIsVector(ll: LatLon): Vector = Vector(ll.lon.lon, ll.lat.lat)

    implicit def vectorIsLatLon(v: Vector): LatLon = LatLon(Lon(v.x1), Lat(v.x2))
  }

  import algebra._
  import geo._

  val latLon2Hex = shearY(Degrees(30))
  val hex2LatLon = shearY(Degrees(-30))

  case class Fractional(v: Vector = Vector()) extends AnyVal

  implicit class LatLonOps(val ll: LatLon) extends AnyVal {

    def toHex: Fractional = Fractional(latLon2Hex(ll))
  }

  implicit def hexFractionalIsVector(hex: Fractional): Vector = hex.v

  implicit class HexFractionalOps(val hex: Fractional) {

    def toLatLon: LatLon = hex2LatLon(hex)

    def round: Cell = Cell(Col(hex.v.x1.round), Row(hex.v.x2.round))
  }

  case class Col(col: Long = 0) extends AnyVal

  case class Row(row: Long = 0) extends AnyVal

  implicit def colIsLong(c: Col): Long = c.col

  implicit def rowIsLong(r: Row): Long = r.row

  case class Cell(col: Col = Col(), row: Row = Row())

  implicit class CellOps(val c: Cell) extends AnyVal {

    def toFractional: Fractional = Fractional(Vector(c.row.toDouble, c.col.toDouble))
  }

  trait Zone {

    def level: Int

    def code: String

    def location: LatLon

    def size: Double

    def geometry: Seq[LatLon]
  }

  implicit class ZoneOps(val z: Zone) extends AnyVal {

    def toWellKnownText: String = z.geometry
      .map(loc => s"${loc.lon.lon} ${loc.lat.lat}")
      .mkString("POLYGON ((", ", ", "))")
  }

  trait Grid {

    def zoneByLocation(geo: LatLon, level: Int = 0): Zone

    def zoneByCode(code: String): Zone

    def encode(geo: LatLon, level: Int = 0): String = zoneByLocation(geo, level).code

    def decode(code: String): LatLon = zoneByCode(code).location
  }

  object TeraHex extends Grid {

    case class CellZone(cells: Seq[Cell], code: String) extends Zone {

      override lazy val level: Int = cells.length - 1

      override lazy val location: LatLon = continuous(cells).toLatLon

      override lazy val size: Double = cellSide(level)

      override def geometry: Seq[LatLon] = ???
    }

    val topLevelCellSide: Double = 360D / 27

    def nestingFactor(level: Int = 0): Double = math.pow(3, -level)

    def cellSide(level: Int = 0) = topLevelCellSide * nestingFactor(level)

    def discrete(x: Fractional, iterations: Int): Seq[Cell] = {

      @tailrec
      def loop(
        iteration: Int = 0,
        zero: Fractional = Fractional(),
        result: List[Cell] = List())
      : List[Cell] =

        if (iteration > iterations) result.reverse
        else {
          val diff = Fractional(x - zero)
          val cell = Fractional(diff.scale(1 / cellSide(iteration))).round
          loop(iteration + 1, cell.toFractional, cell :: result)
        }

      loop()
    }

    def continuous(xs: Seq[Cell]): Fractional = {

      @tailrec
      def loop(
        xs: Seq[Cell],
        result: Fractional = Fractional(),
        level: Int = 0)
      : Fractional = xs match {

        case Nil => result

        case cell :: cells =>
          val shift = cell.toFractional.scale(nestingFactor(level))
          loop(cells, Fractional(result + shift), level + 1)
      }

      loop(xs)
    }

    val dictionary = ('A' to 'Z') ++ ('a' to 'z')

    def encodeCells(cells: Seq[Cell]): String = {
      val (top :: tail) = cells
      val topCode = Seq(top.col.toInt, top.row.toInt).map(dictionary).mkString

      // TODO: consider left and right outliers
      def encodeSubCell(c: Cell): String = (3 * (c.col.toInt + 1) + (c.row.toInt + 1)).toString

      (topCode :: tail.map(encodeSubCell)).mkString
    }

    def decodeCells(code: String): Seq[Cell] = {

      val (topCode, tail) = code.splitAt(2)
      val Array(topCol, topRow) = topCode.map(dictionary.indexOf).toArray
      val top = Cell(Col(topCol), Row(topRow))

      def decodeSubCell(ch: Char): Cell = ???

      top :: tail.toList.map(decodeSubCell)
    }

    override def zoneByLocation(geo: LatLon, level: Int = 0): Zone = {
      val cells = discrete(geo.toHex, level + 1)
      val code = encodeCells(cells)
      CellZone(cells, code)
    }

    override def zoneByCode(code: String): Zone = CellZone(decodeCells(code), code)
  }

}
