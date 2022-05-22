import sbt._

object Libs {

  private val CirceV         = "0.14.2"
  private val TapirV         = "1.0.0-RC1"
  private val Http4sV        = "0.23.11"
  private val TsecV          = "0.4.0"
  private val SttpV          = "3.6.2"
  private val macwireVersion = "2.5.7"
  private val PrometheusV    = "0.15.0"

  val jsonDeps = Seq(
    "io.circe"                    %% "circe-core"       % CirceV,
    "io.circe"                    %% "circe-generic"    % CirceV,
    "io.circe"                    %% "circe-parser"     % CirceV,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % TapirV
  )
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

  val macwireDependencies = Seq(
    "com.softwaremill.macwire" %% "macrosautocats" % macwireVersion
  ).map(_ % Provided)

  val monitoringDeps = Seq(
    "io.prometheus"                  % "simpleclient"             % PrometheusV,
    "io.prometheus"                  % "simpleclient_hotspot"     % PrometheusV,
    "com.softwaremill.sttp.client3" %% "prometheus-backend"       % SttpV,
    "com.softwaremill.sttp.tapir"   %% "tapir-prometheus-metrics" % TapirV
  )

  val coreDeps = Seq(
    "org.typelevel"           %% "cats-effect"     % "3.3.12",
    "com.softwaremill.common" %% "tagging"         % "2.2.1",
    "io.github.jmcardon"      %% "tsec-password"   % TsecV,
    "io.github.jmcardon"      %% "tsec-cipher-jca" % TsecV
  ) ++ macwireDependencies

  val httpDeps = Seq(
    "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"           % TapirV,
    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle"       % TapirV,
    "com.softwaremill.sttp.client3" %% "circe"                         % SttpV,
    "org.http4s"                    %% "http4s-dsl"                    % Http4sV,
    "org.http4s"                    %% "http4s-blaze-server"           % Http4sV,
    "org.http4s"                    %% "http4s-blaze-client"           % Http4sV,
    "org.http4s"                    %% "http4s-circe"                  % Http4sV,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % SttpV,
    "com.softwaremill.sttp.client3" %% "slf4j-backend"                 % SttpV,
    "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub-server"        % TapirV
  ) ++ jsonDeps ++ loggingDeps ++ configDeps

  val allDeps = coreDeps ++ httpDeps ++ unitTestingStack ++ monitoringDeps
}
