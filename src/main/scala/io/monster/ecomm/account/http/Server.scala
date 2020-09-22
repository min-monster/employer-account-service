package io.monster.ecomm.account.http

import cats.arrow.Arrow.ops.toAllArrowOps
import cats.data.Kleisli
import cats.effect.{ ExitCode, Timer }
import cats.implicits._
import io.monster.ecomm.account.configuration.Configuration.HttpServerConfig
import io.monster.ecomm.account.environment.Environments.AppEnvironment
import io.monster.ecomm.account.http.endpoint.{ Accounts, Users }
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.{ AutoSlash, GZip }
import org.http4s.{ HttpRoutes, Request, Response }
import zio.interop.catz._
import zio.{ RIO, ZIO }
import org.http4s.rho.RhoMiddleware
import org.http4s.rho.swagger._
import org.http4s.rho.swagger.models.Info

object Server {
  type ServerRIO[A] = RIO[AppEnvironment, A]
  type ServerRoutes =
    Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]]

  def runServer: ZIO[AppEnvironment, Throwable, Unit] =
    ZIO
      .runtime[AppEnvironment]
      .flatMap { implicit rts =>
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

    import org.http4s.rho.swagger.syntax.io._

    val swaggerMiddleWare: RhoMiddleware[ServerRIO] = SwaggerSupport
      .apply[ServerRIO]
      .createRhoMiddleware(swaggerMetadata =
        SwaggerMetadata(apiInfo =
          Info(
            title = "Employer Account Service",
            version = "0.0.1",
            description = Some("This is a PoC service.")
          )
        )
      )
    val helloRoutes = HelloService.api.toRoutes(swaggerMiddleWare);
    val routes = userRoutes <+> accountRoutes <+> helloRoutes

    Router[ServerRIO](basePath -> routes).orNotFound
  }

  private val middleware: HttpRoutes[ServerRIO] => HttpRoutes[ServerRIO] = { http: HttpRoutes[ServerRIO] =>
    AutoSlash(http)
  }.andThen { http: HttpRoutes[ServerRIO] =>
    GZip(http)
  }
}
