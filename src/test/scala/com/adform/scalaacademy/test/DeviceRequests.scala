package com.adform.scalaacademy.test

import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import com.adform.scalaacademy.device.DeviceApi.DeviceCreateIn
import com.adform.scalaacademy.device.DeviceModel.{Device, DeviceRules}
import com.adform.scalaacademy.util.Id
import com.softwaremill.tagging.@@
import sttp.client3
import sttp.client3.{UriContext, basicRequest}

trait DeviceRequests { self: TestRequestsSupport with TestSupport =>

  def deviceGetReq(id: Id @@ Device): client3.Response[Either[String, String]] = {
    basicRequest
      .get(uri"$basePath/device?deviceId=$id")
      .send(backend)
      .unwrap
  }

  def deviceCreateReq(rules: DeviceRules): client3.Response[Either[String, String]] = {
    basicRequest
      .post(uri"$basePath/device")
      .body(DeviceCreateIn(rules).asJson.noSpaces)
      .send(backend)
      .unwrap
  }
}
