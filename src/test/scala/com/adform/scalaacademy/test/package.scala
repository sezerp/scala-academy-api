package com.adform.scalaacademy

import com.adform.scalaacademy.config.Config
import com.softwaremill.quicklens._

import scala.concurrent.duration._

package object test {
  val DefaultConfig: Config = Config.load
  val TestConfig: Config = DefaultConfig
    .modify(_.deviceService.errorSimulator.maxTimeout)
    .setTo(
      1.milliseconds
    ) // cannot be zero, as it will cause throw exception when try select random number between (0, 0)
    .modify(_.deviceService.errorSimulator.errorProbability)
    .setTo(0)
}
