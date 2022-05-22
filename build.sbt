import Libs._
import sbtbuildinfo.BuildInfoKey.action
import scala.util.Try

val scalafixConfig = Seq(
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

val scalaCOpts = Seq(
  "-Ywarn-unused:imports",
  "-language:postfixOps"
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion,
    action("lastCommitHash") {
      import scala.sys.process._
      // if the build is done outside of a git repository, we still want it to succeed
      Try("git rev-parse HEAD".!!.trim).getOrElse("?")
    }
  ),
  buildInfoOptions += BuildInfoOption.ToJson,
  buildInfoOptions += BuildInfoOption.ToMap,
  buildInfoPackage := "com.adform.scalaacademy.version",
  buildInfoObject := "BuildInfo"
)

lazy val rootProject = (project in file("."))
  .settings(
    name := "scala-academy-api",
    scalaVersion := "2.13.8",
    libraryDependencies ++= allDeps,
    scalacOptions ++= scalaCOpts
  )
  .settings(scalafixConfig)
  .enablePlugins(BuildInfoPlugin)
  .settings(buildInfoSettings)
