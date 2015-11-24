package net.teralytics.terahex


trait Encoding[Code] {

  def encode(zone: Zone): Code

  def decode(code: Code): Zone
}

object Encoding {

  /**
    * Encoding of zones into a decimal number 1[RRR][SSSS...] always prefixed with 1 to ensure fixed digit length,
    * followed by three digits R representing the `Zone.rootSize`, followed by one digit S for each sub cell.
    */
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

    private[this] def encodeRoot(z: Zone): BigInt = {
      val s = z.rootSize.round
      assert(s <= 999 && s > 0, s"Root size must be in (0, 999]")
      1000 + s
    }

    private[this] def decodeRoot(code: Int): Zone = Zone(code - 1000, Seq())

    override def encode(zone: Zone): BigInt =
      zone.cells
        .foldLeft(encodeRoot(zone))((code, cell) => code * 10 + reverseDictionary(cell))

    override def decode(code: BigInt): Zone = {
      val (root, subs) = code.toString.splitAt(4)
      val cells = subs.map(ch => dictionary(ch.asDigit))
      decodeRoot(root.toInt).copy(cells = cells)
    }
  }
}
