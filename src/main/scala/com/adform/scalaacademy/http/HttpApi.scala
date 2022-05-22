package com.adform.scalaacademy.http

import cats.effect.{IO, Resource}
import com.adform.scalaacademy.infrastructure.CorrelationIdInterceptor
import com.adform.scalaacademy.logging.FLogger
import com.adform.scalaacademy.util.ServerEndpoints
import com.typesafe.scalalogging.StrictLogging
import org.http4s.HttpRoutes
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.swagger.SwaggerUIOptions
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import org.http4s.blaze.server.BlazeServerBuilder
import sttp.tapir.EndpointInput
import sttp.tapir.emptyInput

class HttpApi(
    http: Http,
    endpoints: ServerEndpoints,
    adminEndpoints: ServerEndpoints,
    prometheusMetrics: PrometheusMetrics[IO],
    config: HttpConfig
) extends StrictLogging {
  private val apiContextPath = List("api", "v1")

  val serverOptions: Http4sServerOptions[IO] = Http4sServerOptions
    .customiseInterceptors[IO]
    .prependInterceptor(CorrelationIdInterceptor)
    .defaultHandlers(msg => ValuedEndpointOutput(http.jsonErrorOutOutput, ErrorOut(msg)), notFoundWhenRejected = true)
    .serverLog {
      val flogger = new FLogger(logger)
      Http4sServerOptions
        .defaultServerLog[IO]
        .doLogWhenHandled((msg, e) => e.fold(flogger.debug[IO](msg))(flogger.debug(msg, _)))
        .doLogAllDecodeFailures((msg, e) => e.fold(flogger.debug[IO](msg))(flogger.debug(msg, _)))
        .doLogExceptions((msg, e) => flogger.error[IO](msg, e))
        .doLogWhenReceived(msg => flogger.debug[IO](msg))
    }
    .corsInterceptor(CORSInterceptor.default[IO])
    .metricsInterceptor(prometheusMetrics.metricsInterceptor())
    .options

  lazy val routes: HttpRoutes[IO] = Http4sServerInterpreter(serverOptions).toRoutes(allEndpoints)

  lazy val allEndpoints: List[ServerEndpoint[Any, IO]] = {
    val docsEndpoints =
      SwaggerInterpreter(swaggerUIOptions = SwaggerUIOptions.default.copy(contextPath = apiContextPath))
        .fromServerEndpoints(endpoints.toList, "Scala-Academy-API", "1.0")

    val apiEndpoints =
      (endpoints ++ docsEndpoints).map(se =>
        se.prependSecurityIn(apiContextPath.foldLeft(emptyInput: EndpointInput[Unit])(_ / _))
      )

    val allAdminEndpoints =
      (adminEndpoints ++ List(prometheusMetrics.metricsEndpoint)).map(_.prependSecurityIn("admin"))

    apiEndpoints.toList ++ allAdminEndpoints.toList
  }

  lazy val resource: Resource[IO, org.http4s.server.Server] = BlazeServerBuilder[IO]
    .bindHttp(config.port, config.host)
    .withHttpApp(routes.orNotFound)
    .resource
}
