package net.teralytics.terahex

trait Encoding {

  def encode(cell: Cell): String

  def decode(code: String): Cell
}

object Encoding {

  def topLevel = new Encoding {

    val dictionary = ('A' to 'Z') ++ ('a' to 'z')
    val reverseDictionary = dictionary.zipWithIndex.toMap

    override def encode(cell: Cell): String =
      Seq(cell.col.toInt, cell.row.toInt)
        .map(dictionary)
        .mkString

    override def decode(code: String): Cell = {
      val Seq(topCol, topRow) = code.map(reverseDictionary)
      Cell(Col(topCol), Row(topRow))
    }
  }

  def subCell = new Encoding {

    val dictionary = IndexedSeq(
      Cell(Col(0), Row(0)),
      Cell(Col(-2), Row(1)),
      Cell(Col(-1), Row(0)),
      Cell(Col(-1), Row(1)),
      Cell(Col(0), Row(-1)),
      Cell(Col(0), Row(1)),
      Cell(Col(1), Row(0)),
      Cell(Col(1), Row(-1)),
      Cell(Col(2), Row(-1)))

    val reverseDictionary = dictionary.zipWithIndex.toMap

    override def encode(cell: Cell): String = reverseDictionary(cell).toString

    override def decode(code: String): Cell = dictionary(code.toInt)
  }
}
