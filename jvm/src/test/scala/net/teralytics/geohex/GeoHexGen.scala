package net.teralytics.geohex

import org.scalacheck.Gen
import org.scalacheck.Gen._

object GeoHexGen {

  val allLevels = 0 to 15
  val topCodeRegex = "[a-zA-Z]{2}"
  val subCodeRegex = "[0-8]{0,15}"
  val codeRegex = s"$topCodeRegex$subCodeRegex"

  def latitude: Gen[Double] = choose(-90.0, 90.0) :| "lat"

  def longitude: Gen[Double] = choose(-180.0, 180.0) :| "lon"

  def location: Gen[(Double, Double)] = Gen.zip(latitude, longitude)

  def level: Gen[Int] = oneOf(allLevels) :| "level"

  def topCodes = {
    val g = for {
      a <- alphaChar
      b <- alphaChar
    } yield s"$a$b"
    g.suchThat(_.matches(s"^$topCodeRegex$$"))
  }

  def subCode(level: Int): Gen[String] =
    listOfN(level, choose('0', '8'))
      .map(_.mkString)

  def code: Gen[String] = {
    val g = for {
      t <- topCodes
      l <- level
      c <- subCode(l)
    } yield t + c
    g.suchThat(_.matches(s"^$codeRegex$$")) :| "code"
  }

}
