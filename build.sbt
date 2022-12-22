ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "finagle-demo",
    libraryDependencies ++= Seq(
      "com.twitter" %% "finagle-core" % "22.7.0" cross CrossVersion.for3Use2_13,
      "com.twitter" %% "finagle-http" % "22.7.0" cross CrossVersion.for3Use2_13
    )
  )
