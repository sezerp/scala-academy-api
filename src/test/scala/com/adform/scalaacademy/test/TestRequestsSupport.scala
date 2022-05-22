package com.adform.scalaacademy.test

import cats.effect.IO
import sttp.client3.SttpBackend

trait TestRequestsSupport {
  protected def basePath: String
  def backend: SttpBackend[IO, Any]
}
