val mainScalaVersion = "2.11.7"

lazy val root = project.in(file(".")).
  aggregate(geohexJS, geohexJVM).
  settings(
    scalaVersion := mainScalaVersion,
    crossScalaVersions := Seq(mainScalaVersion, "2.10.5"),
    publish := {},
    publishLocal := {}
  )

lazy val geohex = crossProject.in(file(".")).
  settings(
    organization := "net.teralytics",
    name := "geohex",
    version := "0.1." + sys.env.getOrElse("TRAVIS_BUILD_NUMBER", "0-SNAPSHOT"),
    scalaVersion := mainScalaVersion,
    licenses +=("MIT", url("http://opensource.org/licenses/MIT"))
  ).
  jvmSettings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
      "io.spray" %% "spray-json" % "1.3.2" % "test"),
    bintrayOrganization := Some("teralytics"),
    bintrayReleaseOnPublish in ThisBuild := false
  ).
  jsSettings(
  )

lazy val geohexJVM = geohex.jvm

lazy val geohexJS = geohex.js
