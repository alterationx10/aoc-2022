ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "aoc-2022",
    projectDependencies += "dev.zio" %% "zio" % "2.0.4",
    projectDependencies += "dev.zio" %% "zio-streams" % "2.0.4"
  )
