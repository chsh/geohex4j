package net.teralytics.terahex


trait Encoding[Code] {

  def encode(zone: Zone): Code

  def decode(code: Code): Zone
}

object Encoding {

  lazy val numeric = new Encoding[BigInt] {

    val dictionary = IndexedSeq(
      Cell.subS,
      Cell.subSW,
      Cell.subW,
      Cell.subSE,
      Cell.subCenter,
      Cell.subNW,
      Cell.subE,
      Cell.subNE,
      Cell.subN)

    val reverseDictionary = dictionary.zipWithIndex.toMap

    private[this] def root(z: Zone): BigInt = {
      val s = z.rootSize.round
      assert(s <= 999 && s > 0, s"Root size must be in (0, 999]")
      1000 + s
    }

    private[this] def rootSize(code: Int): Double = code - 1000

    override def encode(zone: Zone): BigInt =
      zone.cells
        .foldLeft(root(zone))((acc, cell) => acc * 10 + reverseDictionary(cell))

    override def decode(code: BigInt): Zone = {
      val (root, subs) = code.toString.splitAt(4)
      Zone(rootSize(root.toInt), subs.map(ch => dictionary(ch.asDigit)))
    }
  }
}
