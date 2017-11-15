name := "dbgp-interfaces"

organization := "com.codnos"

version := "1.0.1-SNAPSHOT"

description := "Interfaces and implementations for both client and server side of DBGp"

scalaVersion := "2.11.8"

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

fork in Test := true

javaOptions in Test += "-Djava.util.logging.config.file=src/test/resources/logging.properties"

// publishing settings

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-2.0" -> url("https://github.com/Codnos/dbgp-interfaces/blob/master/LICENSE"))

homepage := Some(url("https://github.com/Codnos/dbgp-interfaces"))

pomExtra := (
    <scm>
      <url>https://github.com/Codnos/dbgp-interfaces</url>
      <connection>scm:git:git@github.com:Codnos/dbgp-interfaces.git</connection>
    </scm>
    <developers>
      <developer>
        <id>ligasgr</id>
        <name>Grzegorz Ligas</name>
        <email>grzegorz.ligas@codnos.com</email>
        <url>https://github.com/ligasgr</url>
      </developer>
    </developers>)