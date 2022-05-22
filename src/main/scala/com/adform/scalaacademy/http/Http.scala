package com.adform.scalaacademy.http

import cats.effect.IO
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxEitherId}
import com.softwaremill.tagging.{@@, Tagger}
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{Codec, EndpointOutput, PublicEndpoint, Schema, SchemaType, Tapir}
import tsec.common.SecureRandomId
import com.adform.scalaacademy.util._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.TapirJsonCirce
import com.adform.scalaacademy.infrastructure.JsonSupport._
import com.adform.scalaacademy.logging.FLogging
import sttp.model.StatusCode
import com.adform.scalaacademy.Fail
import io.circe.Printer

class Http() extends Tapir with TapirJsonCirce with TapirSchemas with FLogging {
  val jsonErrorOutOutput: EndpointOutput[ErrorOut]       = jsonBody[ErrorOut]
  val failOutput: EndpointOutput[(StatusCode, ErrorOut)] = statusCode.and(jsonErrorOutOutput)
  val baseEndpoint: PublicEndpoint[Unit, (StatusCode, ErrorOut), Unit, Any] =
    endpoint.errorOut(failOutput)

  private val InternalServerError = (StatusCode.InternalServerError, "Internal server error")
  private val failToResponseData: Fail => (StatusCode, String) = {
    case Fail.NotFound(what)      => (StatusCode.NotFound, what)
    case Fail.Conflict(msg)       => (StatusCode.Conflict, msg)
    case Fail.IncorrectInput(msg) => (StatusCode.BadRequest, msg)
    case Fail.Forbidden           => (StatusCode.Forbidden, "Forbidden")
    case Fail.Unauthorized(msg)   => (StatusCode.Unauthorized, msg)
    case _                        => InternalServerError
  }

  implicit class IOOut[T](f: IO[T]) {
    def toOut: IO[Either[(StatusCode, ErrorOut), T]] = {
      f.map(t => t.asRight[(StatusCode, ErrorOut)]).recoverWith { case f: Fail =>
        val (statusCode, message) = failToResponseData(f)
        logger.warn[IO](s"Request fail: $message").map(_ => (statusCode, ErrorOut(message)).asLeft[T])
      }
    }
  }

  override def jsonPrinter: Printer = noNullsPrinter
}

trait TapirSchemas {
  implicit val idPlainCodec: PlainCodec[SecureRandomId] =
    Codec.string.map(_.asInstanceOf[SecureRandomId])(identity)

  implicit def taggedPlainCodec[U, T](implicit uc: PlainCodec[U]): PlainCodec[U @@ T] =
    uc.map(_.taggedWith[T])(identity)

  implicit val schemaForId: Schema[Id]                                       = Schema(SchemaType.SString[Id]())
  implicit def schemaForTagged[U, T](implicit uc: Schema[U]): Schema[U @@ T] = uc.asInstanceOf[Schema[U @@ T]]
}

case class ErrorOut(error: String)
