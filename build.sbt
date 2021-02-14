val Http4sVersion = "0.18.0"
val Specs2Version = "4.0.2"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.12.3"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.tristanjl",
    name := "device-api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "commons-codec" % "commons-codec" % "1.15"
    )
  )

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")
libraryDependencies += "org.typelevel" %% "spire" % "0.14.1"

scalacOptions += "-language:higherKinds"
