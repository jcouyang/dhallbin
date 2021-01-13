val Http4sVersion = "1.0.0-M4"
val MunitVersion = "0.7.11"
val TwitterVersion = "20.8.0"
val CirisVersion = "1.1.2"
val DoobieVersion = "0.9.0"
val CirceVersion = "0.13.0"

ThisBuild / scalaVersion := "2.13.3"
lazy val root = (project in file("."))
  .settings(
    organization := "us.oyanglul",
    name := "dhallbin",
    libraryDependencies ++= Seq(
      "org.http4s"                 %% "http4s-blaze-client"            % Http4sVersion,
      "org.http4s"                 %% "http4s-circe"                   % Http4sVersion,
      "org.http4s"                 %% "http4s-dsl"                     % Http4sVersion,
      "org.http4s"                 %% "http4s-finagle"                 % Http4sVersion,
      "io.circe"                   %% "circe-generic"                  % CirceVersion,
      "com.twitter"                %% "twitter-server"                 % TwitterVersion,
      "com.twitter"                %% "twitter-server-logback-classic" % TwitterVersion,
      "com.twitter"                %% "finagle-stats"                  % TwitterVersion,
      "us.oyanglul"                %% "dhall-generic"                  % "0.2.0",
      "us.oyanglul"                %% "dhall-imports"                  % "0.7.0-M1",
      "org.dhallj"                  % "dhall-yaml"                     % "0.7.0-M1",
      "ch.qos.logback"              % "logback-classic"                % "1.2.3",
      "org.scalameta"              %% "munit"                          % MunitVersion % Test,
      "org.scalameta"              %% "munit-scalacheck"               % MunitVersion % Test,
      "org.mockito"                %% "mockito-scala"                  % "1.15.0"     % Test,
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.14"      % "1.2.3"      % Test,
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
    addCompilerPlugin(scalafixSemanticdb),
    addCommandAlias(
      "rmUnused",
      """set scalacOptions -= "-Xfatal-warnings";scalafix RemoveUnused;set scalacOptions += "-Xfatal-warnings"""",
    ),
  )
