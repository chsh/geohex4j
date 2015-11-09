
name := "geohex"

organization := "net.teralytics"

version := "0.1." + sys.env.getOrElse("TRAVIS_BUILD_NUMBER", "0-SNAPSHOT")

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.11.7", "2.10.5")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization := Some("teralytics")

bintrayReleaseOnPublish in ThisBuild := false
