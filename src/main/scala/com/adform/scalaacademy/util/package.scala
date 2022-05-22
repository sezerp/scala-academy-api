package com.adform.scalaacademy

import cats.data.NonEmptyList
import cats.effect.IO
import com.softwaremill.tagging.@@
import sttp.tapir.server.ServerEndpoint
import tsec.common.SecureRandomId

package object util {
  type Id = SecureRandomId

  implicit class RichString(val s: String) extends AnyVal {
    def asId[T]: Id @@ T = s.asInstanceOf[Id @@ T]
  }

  type ServerEndpoints = NonEmptyList[ServerEndpoint[Any, IO]]
}
