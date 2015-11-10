package net.teralytics.geohex

import scala.math._

object Encoding {

  private[this] val h_key = ('A' to 'Z') ++ ('a' to 'z')

  def decodeXY(code: String): (Long, Long) = {
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
}
