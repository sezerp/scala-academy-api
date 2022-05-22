package com.adform.scalaacademy.config

import com.adform.scalaacademy.device.DeviceServiceConf
import com.adform.scalaacademy.http.HttpConfig
import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Config(http: HttpConfig, deviceService: DeviceServiceConf)

object Config extends StrictLogging {
  def load: Config = ConfigSource.default.loadOrThrow[Config]

  def log(config: Config): Unit = {
    val info = s"""
                      |App configuration:
                      |-----------------------
                      |API:              ${config.http}
                      |ERROR simulator:  ${config.deviceService.errorSimulator}
                      |
                      |-----------------
                      |""".stripMargin

    logger.info(info)
  }
}
