package com.adform.scalaacademy.metrics

import io.prometheus.client.hotspot

object Metrics {

  def init(): Unit = {
    hotspot.DefaultExports.initialize()
  }
}
