name := "cats-scala-rest-api"

version := "0.1"

scalaVersion := "2.12.13"

//triggeredMessage in ThisBuild := Watched.clearWhenTriggered

//idePackagePrefix := Some("com.github.nullptr7")

val Http4sVersion = "0.21.20"
val Specs2Version = "4.10.6"
val LogbackVersion = "1.2.3"
val CirceVersion = "0.13.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.github.nullptr7",
    name := "cats-scala-rest-api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.13",
    idePackagePrefix := Some("com.github.nullptr7"),
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-literal"       % CirceVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "com.slamdata" 	  %% "matryoshka-core"	   % "0.18.3",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    )
  )

resolvers += Resolver.sonatypeRepo("releases")

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ypartial-unification")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.10")
libraryDependencies += "org.typelevel" %% "spire" % "0.17.0"