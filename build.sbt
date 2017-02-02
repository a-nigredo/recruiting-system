import sbt.Keys.organization

name := "rs"

mainClass in assembly := Some("com.levi9.RecruitingSystem")

version := "1.0"

scalaVersion := "2.11.8"

assemblyJarName in assembly := "rs.jar"

val akkaVersion = "2.4.14"
val akkaHttpVersion = "10.0.0"
val akkaLogVersion = "2.4.14"
val mysqlConnectorVersion = "5.1.21"
val slickVersion = "3.1.1"
val logbackVersion = "1.0.0"
val accordValidationVersion = "0.6"
val scalaTestVersion = "3.0.0"
val mockitoVersion = "1.9.5"
val h2Version = "1.4.190"
val ldapVersion = "3.1.0"
val scalazVersion = "7.2.6"
val hashidsVersion = "1.0.0"
val scalaGuiceVersion = "4.0.1"
val flywayVersion = "2.3.1"
val scalaLoggingVersion = "3.1.0"
val paradiseVersion = "2.1.0"
val scalaCompilerVersion = "2.11.8"
val json4sVersion = "3.4.0"

lazy val rootDependencies = Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-slf4j" % akkaLogVersion,
  "mysql" % "mysql-connector-java" % mysqlConnectorVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime",
  "com.wix" %% "accord-core" % accordValidationVersion,
  "org.mockito" % "mockito-all" % mockitoVersion % "test",
  "com.h2database" % "h2" % h2Version % "test",
  "com.unboundid" % "unboundid-ldapsdk" % ldapVersion,
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "com.timesprint" %% "hashids-scala" % hashidsVersion,
  "net.codingwell" %% "scala-guice" % scalaGuiceVersion,
  "com.googlecode.flyway" % "flyway-core" % flywayVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "org.json4s" %% "json4s-native" % json4sVersion,
  "org.json4s" %% "json4s-ext" % json4sVersion
)

lazy val commonSettings = Seq(
  organization := "com.levi9",
  version := "0.1",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-deprecation", "-language:implicitConversions", "-language:higherKinds", "-unchecked"),
  libraryDependencies := {
    Seq("org.scala-lang" % "scala-compiler" % scalaCompilerVersion,
      "org.scalactic" %% "scalactic" % scalaTestVersion % "test",
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
      compilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)
    )
  }
)

lazy val `macro` = (project in file("macro"))
  .settings(commonSettings: _*)
  .settings(name := "macro")

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(name := "rs", libraryDependencies ++= rootDependencies)
  .aggregate(`macro`)
  .dependsOn(`macro`)