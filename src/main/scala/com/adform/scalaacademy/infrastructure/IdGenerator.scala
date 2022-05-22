package com.adform.scalaacademy.infrastructure

import cats.effect.Sync
import com.adform.scalaacademy.util.Id
import com.softwaremill.tagging._
import tsec.common.SecureRandomId

trait IdGenerator {
  def nextId[F[_]: Sync, U](): F[Id @@ U]
}

object DefaultIdGenerator extends IdGenerator {
  override def nextId[F[_]: Sync, U](): F[Id @@ U] = Sync[F].delay { SecureRandomId.Strong.generate.taggedWith[U] }
}
