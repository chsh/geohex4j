package net.teralytics.terahex

import com.vividsolutions.jts.geom.Geometry
import org.scalatest.enablers.Containing
import org.scalatest.matchers.{ MatchResult, Matcher }

trait GeometryMatchers {

  def matcherPrecision: Double = 1e-5

  def intersect(other: Geometry) = new Matcher[Geometry] {
    override def apply(left: Geometry) = MatchResult(
      left.intersects(other),
      s"\n$left\ndid not intersect\n$other",
      s"\n$left\nintersects\n$other")
  }

  def cover(other: Geometry) = new Matcher[Geometry] {
    override def apply(left: Geometry) = MatchResult(
      left.buffer(matcherPrecision).covers(other),
      s"\n$left\ndid not cover\n$other",
      s"\n$left\ncovers\n$other")
  }

  implicit def containingNatureOfGeometry = new Containing[Geometry] {

    override def contains(container: Geometry, element: Any): Boolean = element match {
      case g: Geometry => container.buffer(matcherPrecision).contains(g)
      case _ => false
    }

    override def containsOneOf(container: Geometry, elements: Seq[Any]): Boolean =
      elements.exists(contains(container, _))

    override def containsNoneOf(container: Geometry, elements: Seq[Any]): Boolean =
      elements.forall(!contains(container, _))
  }
}
