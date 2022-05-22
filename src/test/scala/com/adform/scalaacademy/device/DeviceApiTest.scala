package com.adform.scalaacademy.device

import com.adform.scalaacademy.device.DeviceApi.{DeviceCreateOut, DeviceGetOut}
import com.adform.scalaacademy.device.DeviceModel.{DeviceRules, Rule}
import com.adform.scalaacademy.test.{TestBase, TestDependencies, TestSupport}
import org.scalatest.concurrent.Eventually
import com.adform.scalaacademy.infrastructure.JsonSupport._
import sttp.model.StatusCode

class DeviceApiTest extends TestBase with Eventually with TestDependencies with TestSupport {

  "[POST] api/v1//device call create with correct input" should "return created device" in {
    // given
    val rules       = List(Rule(0, 2.5, 5.0), Rule(1, 10.0, 15.0))
    val deviceRules = DeviceRules(rules)
    // when
    val response = requests.deviceCreateReq(deviceRules)
    // then
    response.code shouldBe StatusCode.Ok
    response.body.shouldDeserializeTo[DeviceCreateOut]
    response.body.shouldDeserializeTo[DeviceCreateOut].device.id should fullyMatch regex idPattern
    response.body.shouldDeserializeTo[DeviceCreateOut].device.rules shouldBe deviceRules
  }

  "[GET] api/v1//device" should "return created device with given ID" in {
    // given
    val rules       = List(Rule(0, 2.5, 5.0), Rule(1, 10.0, 15.0))
    val deviceRules = DeviceRules(rules)
    // when
    val createResponse = requests.deviceCreateReq(deviceRules).body.shouldDeserializeTo[DeviceCreateOut]
    requests.deviceCreateReq(deviceRules)
    requests.deviceCreateReq(deviceRules)
    val getResponse = requests.deviceGetReq(createResponse.device.id)
    // then
    getResponse.code shouldBe StatusCode.Ok
    getResponse.body.shouldDeserializeTo[DeviceGetOut]
    getResponse.body.shouldDeserializeTo[DeviceGetOut].device.id should fullyMatch regex idPattern
    getResponse.body.shouldDeserializeTo[DeviceGetOut].device.rules shouldBe deviceRules
  }
}
