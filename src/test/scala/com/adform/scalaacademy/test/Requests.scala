package com.adform.scalaacademy.test

import cats.effect.IO
import sttp.client3.SttpBackend

class Requests(override val backend: SttpBackend[IO, Any])
    extends TestSupport
    with TestRequestsSupport
    with DeviceRequests {
  override protected val basePath: String = s"http://localhost:${TestConfig.http.port}/api/v1"
}
