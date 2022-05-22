package com.adform.scalaacademy.device

import com.adform.scalaacademy.util.Id
import com.softwaremill.tagging.@@

import java.time.Instant

object DeviceModel {
  case class Device(id: Id @@ Device, rules: DeviceRules, createdAt: Instant)
  case class DeviceRules(rules: List[Rule])
  case class Rule(sensor: Int, warningLevel: Double, criticalLevel: Double)
}
