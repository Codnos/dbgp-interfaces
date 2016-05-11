name := "dbgp-interfaces"

organization := "com.codnos.dbgp-interfaces"

version := "1.0-SNAPSHOT"

description := "Interfaces and implementations for both client and server side of DBGp"

publishMavenStyle := true

crossPaths := false

autoScalaLibrary := false

libraryDependencies ++= Seq(
  "io.netty" % "netty-all" % "4.1.0.Beta7",
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.hamcrest" % "hamcrest-library" % "1.1" % "test" exclude("org.hamcrest", "hamcrest-core"),
  "com.jayway.awaitility" % "awaitility" % "1.6.1" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)
