import sbt._

object Libs {

  private val CirceV  = "0.13.0"
  private val TapirV  = "0.17.19"
  private val Http4sV = "0.21.23"
  private val TsecV   = "0.2.1"
  private val SttpV       = "3.3.4"

  val jsonDeps = Seq(
    "io.circe" %% "circe-core"    % CirceV,
    "io.circe" %% "circe-generic" % CirceV,
    "io.circe" %% "circe-parser"  % CirceV
  )
  val httpDeps = Seq()
  val loggingDeps = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
    "ch.qos.logback"              % "logback-classic" % "1.2.3",
    "org.codehaus.janino"         % "janino"          % "3.1.0",
    "de.siegmar"                  % "logback-gelf"    % "2.2.0"
  )
  val unitTestingStack = Seq(
    "org.scalatest"              %% "scalatest" % "3.1.1"  % Test,
    "com.softwaremill.quicklens" %% "quicklens" % "1.4.12" % Test
  )
  val configDeps = Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.17.1"
  )

  val coreDeps = Seq(
    "io.monix"                %% "monix"           % "3.4.0",
    "com.softwaremill.common" %% "tagging"         % "2.2.1",
    "org.postgresql"           % "postgresql"      % "42.3.1", // just for migration
    "io.github.jmcardon"      %% "tsec-password"   % TsecV,
    "io.github.jmcardon"      %% "tsec-cipher-jca" % TsecV,
    "io.micrometer"            % "micrometer-core" % "1.8.3"
  )

  val webDeps = Seq(
    "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"              % TapirV,
    "com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml"        % TapirV,
    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-http4s"         % TapirV,
    "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"                % TapirV,
    "com.softwaremill.sttp.client3" %% "circe"                           % SttpV,
    "org.http4s"                    %% "http4s-dsl"                      % Http4sV,
    "org.http4s"                    %% "http4s-blaze-server"             % Http4sV,
    "org.http4s"                    %% "http4s-blaze-client"             % Http4sV,
    "org.http4s"                    %% "http4s-circe"                    % Http4sV,
    "org.http4s"                    %% "http4s-prometheus-metrics"       % Http4sV,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-monix" % SttpV,
    "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"             % TapirV,
    "com.softwaremill.sttp.client3" %% "slf4j-backend"                   % SttpV
  ) ++ jsonDeps
}
