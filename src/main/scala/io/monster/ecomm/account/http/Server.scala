package io.monster.ecomm.account.http

import cats.arrow.Arrow.ops.toAllArrowOps
import cats.data.Kleisli
import cats.effect.{ExitCode, Timer}
import cats.implicits._
import io.monster.ecomm.account.configuration.Configuration.HttpServerConfig
import io.monster.ecomm.account.environment.Environments.AppEnvironment
import io.monster.ecomm.account.http.endpoint.{Accounts, Users}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.{AutoSlash, GZip}
import org.http4s.{HttpRoutes, Request, Response}
import zio.interop.catz._
import zio.{RIO, ZIO}

object Server {
  type ServerRIO[A] = RIO[AppEnvironment, A]
  type ServerRoutes = Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]]

  def runServer: ZIO[AppEnvironment, Throwable, Unit] =
    ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
      val cfg = rts.environment.get[HttpServerConfig]
      val ec = rts.platform.executor.asEC
      val timer = Timer

      BlazeServerBuilder[ServerRIO]
        .bindHttp(cfg.port, cfg.host)
        .withHttpApp(createRoutes(cfg.path))
        .serve
        .compile[ServerRIO, ServerRIO, ExitCode]
        .drain
    }
      .orDie

  def createRoutes(basePath: String): ServerRoutes = {
    val userRoutes = Users.routes
    val accountRoutes = Accounts.routes
    val routes = userRoutes <+> accountRoutes

    Router[ServerRIO](basePath -> middleware(routes)).orNotFound
  }

  private val middleware: HttpRoutes[ServerRIO] => HttpRoutes[ServerRIO] = {
    { http: HttpRoutes[ServerRIO] =>
      AutoSlash(http)
    }.andThen { http: HttpRoutes[ServerRIO] =>
      GZip(http)
    }
  }
}