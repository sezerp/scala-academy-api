package com.adform.scalaacademy.device

import cats.data.NonEmptyList
import com.adform.scalaacademy.device.DeviceModel.{Device, DeviceRules}
import com.adform.scalaacademy.http.Http
import com.adform.scalaacademy.infrastructure.JsonSupport._
import com.adform.scalaacademy.util.{Id, ServerEndpoints}
import com.softwaremill.tagging.@@
import sttp.tapir.EndpointInput
import sttp.tapir.generic.auto._

class DeviceApi(http: Http, deviceService: DeviceService) {
  private val Path = "device"

  import http._
  import DeviceApi._

  private val getQuery: EndpointInput[Id @@ Device] =
    query[String]("deviceId").map(_.asInstanceOf[Id @@ Device])(_.asInstanceOf[String])
  private val getDevice = baseEndpoint.get
    .in(Path)
    .in(getQuery)
    .out(jsonBody[DeviceGetOut])
    .serverLogic { id =>
      (for {
        device <- deviceService.get(id)
      } yield DeviceGetOut(device)).toOut
    }

  private val createDevice = baseEndpoint.post
    .in(Path)
    .in(jsonBody[DeviceCreateIn])
    .out(jsonBody[DeviceCreateOut])
    .serverLogic { in =>
      (for {
        device <- deviceService.create(in.rules)
      } yield DeviceCreateOut(device)).toOut
    }

  val endpoints: ServerEndpoints = NonEmptyList.of(getDevice, createDevice).map(_.tag("device"))

}

object DeviceApi {
  case class DeviceGetOut(device: Device)
  case class DeviceCreateIn(rules: DeviceRules)
  case class DeviceCreateOut(device: Device)
}
