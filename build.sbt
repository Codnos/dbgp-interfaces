name := "dbgp-interfaces"

organization := "com.codnos.dbgp-interfaces"

version := "1.0-SNAPSHOT"

description := "Interfaces and implementations for both client and server side of DBGp"

scalaVersion := "2.11.8"

publishMavenStyle := true

crossPaths := false

autoScalaLibrary := false

resolvers += "Sonatype" at "https://oss.sonatype.org/content/groups/public/"

libraryDependencies ++= Seq(
  "io.netty" % "netty-all" % "4.1.0.Beta7",
  "junit" % "junit" % "4.12" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "org.hamcrest" % "hamcrest-library" % "1.3" % "test" exclude("org.hamcrest", "hamcrest-core"),
  "com.jayway.awaitility" % "awaitility" % "1.7.0" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.xmlunit" % "xmlunit-core" % "2.1.1" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
