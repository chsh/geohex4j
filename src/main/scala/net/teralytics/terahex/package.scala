package net.teralytics

package object terahex {

  implicit class ZoneOps(val z: Zone) extends AnyVal {

    def toWellKnownText: String = {
      val points = z.geometry
      (points :+ points.head)
        .map(loc => f"${loc.lon.lon}%f ${loc.lat.lat}%f")
        .mkString("POLYGON ((", ", ", "))")
    }

    def code[Code](implicit encoding: Encoding[Code]): Code = encoding.encode(z)
  }

}
