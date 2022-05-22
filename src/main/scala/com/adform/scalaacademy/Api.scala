package com.adform.scalaacademy

import cats.effect.unsafe.implicits.global
import cats.effect.IO
import com.adform.scalaacademy.config.Config
import com.adform.scalaacademy.metrics.Metrics
import com.adform.scalaacademy.util.DefaultClock
import com.typesafe.scalalogging.StrictLogging

object Api extends App with StrictLogging {

  Metrics.init()
  Thread.setDefaultUncaughtExceptionHandler((t, e) => logger.error("Uncaught exception in thread: " + t, e))
  val config = Config.load
  Config.log(config)

  Dependencies
    .wire(config, DefaultClock)
    .use { case Dependencies(httpApi) =>
      httpApi.resource.use(_ => IO.never)
    }
    .unsafeRunSync()
}
