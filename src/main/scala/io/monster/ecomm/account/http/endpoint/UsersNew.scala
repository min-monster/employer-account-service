package io.monster.ecomm.account.http.endpoint

import io.circe.generic.auto._
import io.circe.{ Decoder, Encoder }
import io.monster.ecomm.account.environment.Environments.AppEnvironment
import io.monster.ecomm.account.model.User
import io.monster.ecomm.account.repository._
import org.http4s.circe.{ jsonEncoderOf, jsonOf }
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
// import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import zio.RIO
import zio.interop.catz._
import org.http4s.rho.RhoRoutes
import org.http4s.rho.swagger.SwaggerSupport
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder

object UsersNew {
  type UserNewTask[A] = RIO[AppEnvironment, A]

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[UserNewTask, A] =
    jsonOf[UserNewTask, A]

  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[UserNewTask, A] =
    jsonEncoderOf[UserNewTask, A]

  val dsl: Http4sDsl[UserNewTask] = Http4sDsl[UserNewTask]

  import dsl._

  val swaggerSupport = SwaggerSupport.apply[UserNewTask]
  import swaggerSupport._

  val api = new RhoRoutes[UserNewTask] {
    val user = "user" @@ GET / "userNew"

    "Tagging a API" **
      List("post", "stuff") @@
        user |>> { () =>
      getAll.foldM(
          err => NotFound(),
          users => Ok(users)
      )
    }

    "Get int form path var and also request params" **
      user / pathVar[Int]("id", "user Id")  |>> { (userId: Int) =>
        get(userId).foldM(
            err => NotFound(),
            user => Ok(user)
        )
    }

    "Some test endpoint " **
      user / "somePath2" |>> { () =>
      Ok("result")
    }

  }

}
