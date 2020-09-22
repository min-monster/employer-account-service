package io.monster.ecomm.account.http

import io.monster.ecomm.account.environment.Environments.AppEnvironment
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.rho.RhoRoutes
import org.http4s.server.Router
import zio.interop.catz._
import zio.{ IO, RIO }
import org.http4s.rho.swagger.{ SwaggerSupport, SwaggerSyntax }
import org.http4s.rho.RhoMiddleware

object HelloService {
  type HelloTask[A] = RIO[AppEnvironment, A]

  private val dsl = Http4sDsl[HelloTask]
  import dsl._

  val swaggerSupport = SwaggerSupport.apply[HelloTask]
  import swaggerSupport._

  val api = new RhoRoutes[HelloTask] {
    // We want to define this chunk of the service as abstract for reuse below
    val hello = "hello" @@ GET / "hello"

    "Get int form path var and also request params" **
      hello / "somePath" / pathVar[Int]("someInt", "parameter description") +? paramD[String](
      "name",
      "parameter description"
    ) |>> { (someInt: Int, name: String) =>
      Ok("result")
    }

    "Some test endpoint " **
      hello / "somePath2" |>> { () =>
      Ok("result")
    }

    "Tagging a API" **
      List("post", "stuff") @@
        hello |>> { () =>
      Ok("world")
    }
  }
}
