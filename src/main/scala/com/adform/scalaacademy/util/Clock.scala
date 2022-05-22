package com.adform.scalaacademy.util

import cats.effect.{Sync, Clock => CatsClock}
import cats.implicits.toFunctorOps

import java.time.Instant

trait Clock {
  def now[F[_]: Sync](): F[Instant]
}

import java.time.Instant

object DefaultClock extends Clock {

  override def now[F[_]: Sync](): F[Instant] = {
    for {
      now <- CatsClock[F].realTime.map(_.length)
      instant = Instant.ofEpochMilli(now)
    } yield instant
  }
}
