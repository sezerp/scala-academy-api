package com.adform.scalaacademy

import cats.data.NonEmptyList
import cats.effect.{IO, Resource}
import com.adform.scalaacademy.config.Config
import com.adform.scalaacademy.device.DeviceApi
import com.adform.scalaacademy.http.{Http, HttpApi, HttpConfig}
import com.adform.scalaacademy.infrastructure.DefaultIdGenerator
import com.adform.scalaacademy.metrics.VersionApi
import com.adform.scalaacademy.util.Clock
import com.softwaremill.macwire.autocats.autowire
import io.prometheus.client.CollectorRegistry
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics

case class Dependencies(api: HttpApi)

object Dependencies {
  def wire(
      config: Config,
      clock: Clock
  ): Resource[IO, Dependencies] = {
    def buildHttpApi(http: Http, deviceApi: DeviceApi, versionApi: VersionApi, cfg: HttpConfig) = {
      val prometheusMetrics = PrometheusMetrics.default[IO](registry = new CollectorRegistry())

      new HttpApi(
        http,
        deviceApi.endpoints,
        NonEmptyList.of(versionApi.versionEndpoint),
        prometheusMetrics,
        cfg
      )
    }

    autowire[Dependencies](
      config.http,
      config.deviceService.errorSimulator,
      DefaultIdGenerator,
      clock,
      buildHttpApi _
    )
  }
}
