package net.teralytics.geohex

import scala.collection.mutable
import scala.math._

object Encoding {

  private[this] val h_key = ('A' to 'Z') ++ ('a' to 'z')

  def decode(code: String): (Long, Long) = {
    val dec9 = toNumeric(code)

    val dec3 = dec9
      .map(_.toString.toInt)
      .map(Integer.toString(_, 3))
      .map { x => if (x.length > 1) x else s"0$x" }
      .mkString

    val decXY = dec3.toArray.grouped(2)
      .map { case Array(x, y) => (x, y) }
      .toIndexedSeq

    decXY.zipWithIndex
      .foldLeft((0L, 0L)) {
        case ((xAcc, yAcc), ((xChar, yChar), i)) =>
          val h_pow = pow(3, code.length - i).toLong
          val x =
            if (xChar == '0') xAcc - h_pow
            else if (xChar == '2') xAcc + h_pow
            else xAcc
          val y =
            if (yChar == '0') yAcc - h_pow
            else if (yChar == '2') yAcc + h_pow
            else yAcc
          (x, y)
      }
  }

  private[this] def toNumeric(code: String): String = {
    val alpha = h_key.indexOf(code(0)) * 30 + h_key.indexOf(code(1))
    val dec9 = alpha.toString + code.substring(2)
    val replace = """([15])([^125])(\d*)""".r
    val unpadded = dec9 match {
      case replace(a, _*) if a == "5" => "7" + dec9.tail
      case replace(a, _*) if a == "1" => "3" + dec9.tail
      case _ => dec9
    }
    val padding = "0" * (code.length + 1 - unpadded.length)
    padding + unpadded
  }

  def encode(x: Long, y: Long, level: Int): String = {

    val numericCode = encodeToNumeric(x, y, level)
    topCodeNumToAlpha(numericCode.substring(0, 3)) + numericCode.substring(3)
  }

  private[this] def encodeToNumeric(x: Long, y: Long, level: Int): String = {
    val code3 = mutable.ArrayBuffer[(Int, Int)]()
    var (mod_x, mod_y) = (x.toDouble, y.toDouble)
    val length = level + 2

    for (i <- 0 to length) {
      val h_pow: Double = Math.pow(3, length - i)

      val codeX =
        if (mod_x >= Math.ceil(h_pow / 2)) {
          mod_x -= h_pow
          2
        } else if (mod_x <= -Math.ceil(h_pow / 2)) {
          mod_x += h_pow
          0
        } else 1

      val codeY =
        if (mod_y >= Math.ceil(h_pow / 2)) {
          mod_y -= h_pow
          2
        } else if (mod_y <= -Math.ceil(h_pow / 2)) {
          mod_y += h_pow
          0
        } else 1

      code3.append((codeX, codeY))
    }

    code3.map { case (x3, y3) => Integer.parseInt(s"$x3$y3", 3).toString }
      .mkString
  }

  private[this] def topCodeNumToAlpha(topCodeNum: String): String = {
    val h_1 = topCodeNum.toInt
    val h_a1 = Math.floor(h_1 / 30).toInt
    val h_a2 = h_1 % 30
    h_key.charAt(h_a1).toString + h_key.charAt(h_a2).toString
  }
}
