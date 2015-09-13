name := """reactive-streams-exemples"""

version := "1.0"

scalaVersion := "2.11.6"
val akkaVersion = "1.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "io.projectreactor" % "reactor-stream" % "2.0.4.RELEASE",
  "com.ning" % "async-http-client" % "1.9.29",
  "io.reactivex" % "rxjava-reactive-streams" % "1.0.1",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "io.reactivex" % "rxscala_2.11" % "0.25.0"
)


com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys.withSource := true