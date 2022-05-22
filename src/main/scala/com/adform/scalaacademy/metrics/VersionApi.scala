package com.adform.scalaacademy.metrics

import cats.effect.IO
import com.adform.scalaacademy.http.Http
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.generic.auto._
import com.adform.scalaacademy.infrastructure.JsonSupport._
import com.adform.scalaacademy.version.BuildInfo

class VersionApi(http: Http) {
  private val Path = "version"
  import http._
  import VersionApi._

  val versionEndpoint: ServerEndpoint[Any, IO] = baseEndpoint.get
    .in(Path)
    .out(jsonBody[VersionOut])
    .serverLogic { _ =>
      IO(VersionOut(BuildInfo.lastCommitHash)).toOut
    }
}

object VersionApi {
  case class VersionOut(buildSha: String)
}
