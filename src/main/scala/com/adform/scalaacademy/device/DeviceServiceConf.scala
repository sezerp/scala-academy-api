package com.adform.scalaacademy.device

import scala.concurrent.duration.FiniteDuration

case class DeviceServiceConf(errorSimulator: DeviceErrorSimulatorConf)

case class DeviceErrorSimulatorConf(maxTimeout: FiniteDuration, errorProbability: Double)
