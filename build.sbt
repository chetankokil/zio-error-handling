val currScalaVersion = "3.3.1"

lazy val root = (project in file(".")).settings(
  Seq(
    name         := "test-zio-scala3",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := currScalaVersion,
    scalacOptions ++= Seq(
      "-Xmax-inlines",
      "64"
    ),
    libraryDependencies ++= Seq(
      "dev.zio"                     %% "zio"                     % "2.0.19",
      "dev.zio"                     %% "zio-streams"             % "2.0.19",
      "dev.zio"                     %% "zio-kafka"               % "2.0.7",
      "dev.zio"                     %% "zio-json"                % "0.3.0-RC10",
      "dev.zio"                     %% "zio-logging"             % "2.2.0",
      "dev.zio"                     %% "zio-interop-cats"        % "3.3.0",
      "org.http4s"                  %% "http4s-dsl"              % "0.23.16",
      "org.http4s"                  %% "http4s-server"           % "0.23.25",
      "org.http4s"                  %% "http4s-blaze-server"     % "0.23.16",
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % "1.0.0-RC1",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % "1.0.0-RC1",
      "org.tpolecat"                %% "doobie-core"             % "1.0.0-M5",
      "org.tpolecat"                %% "doobie-hikari"           % "1.0.0-M5",
      "org.tpolecat"                %% "doobie-postgres"         % "1.0.0-M5"
    )
  )
)
