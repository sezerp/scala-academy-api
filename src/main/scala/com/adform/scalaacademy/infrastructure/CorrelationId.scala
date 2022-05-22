package com.adform.scalaacademy.infrastructure

import cats.effect.{IO, IOLocal}
import sttp.client3._
import sttp.client3.{Response, SttpBackend}
import sttp.capabilities.Effect
import sttp.monad.MonadError
import sttp.tapir.server.interceptor.{EndpointInterceptor, RequestHandler, RequestInterceptor, Responder}

import scala.util.Random

object CorrelationId {
  private val localCid = {
    import cats.effect.unsafe.implicits.global
    IOLocal(None: Option[String]).unsafeRunSync()
  }

  private val random = new Random()
  private def generate(): String = {
    def randomUpperCaseChar() = (random.nextInt(91 - 65) + 65).toChar
    def segment               = (1 to 3).map(_ => randomUpperCaseChar()).mkString
    s"$segment-$segment-$segment"
  }

  def get: IO[Option[String]]               = localCid.get
  def set(v: Option[String]): IO[Unit]      = localCid.set(v)
  def setOrNew(v: Option[String]): IO[Unit] = localCid.set(Some(v.fold(generate())(identity)))
}

trait CorrelationIdSource[+F[_]] {
  def get: F[Option[String]]
  def map[T](f: Option[String] => T): F[T]
}

object CorrelationIdSource {
  implicit val forIO: CorrelationIdSource[IO] = new CorrelationIdSource[IO] {
    override def get: IO[Option[String]]               = CorrelationId.get
    override def map[T](f: Option[String] => T): IO[T] = get.map(f)
  }
}

class SetCorrelationIdBackend[P](delegate: SttpBackend[IO, P]) extends SttpBackend[IO, P] {
  override def send[T, R >: P with Effect[IO]](request: Request[T, R]): IO[Response[T]] = {
    CorrelationId.get
      .map {
        case Some(cid) => request.header(CorrelationIdInterceptor.HeaderName, cid)
        case None      => request
      }
      .flatMap(delegate.send)
  }

  override def close(): IO[Unit] = delegate.close()

  override def responseMonad: MonadError[IO] = delegate.responseMonad
}

object CorrelationIdInterceptor extends RequestInterceptor[IO] {
  val HeaderName: String = "X-Correlation-ID"

  override def apply[R, B](
      responder: Responder[IO, B],
      requestHandler: EndpointInterceptor[IO] => RequestHandler[IO, R, B]
  ): RequestHandler[IO, R, B] =
    RequestHandler.from { case (request, endpoints, monad) =>
      val set = CorrelationId.setOrNew(request.header(HeaderName))
      set >> requestHandler(EndpointInterceptor.noop)(request, endpoints)(monad)
    }
}
