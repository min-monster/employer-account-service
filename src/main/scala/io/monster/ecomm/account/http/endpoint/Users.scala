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

object Users {
  type UserTask[A] = RIO[AppEnvironment, A]

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[UserTask, A] =
    jsonOf[UserTask, A]

  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[UserTask, A] =
    jsonEncoderOf[UserTask, A]

  val dsl: Http4sDsl[UserTask] = Http4sDsl[UserTask]

  import dsl._

  val swaggerSupport = SwaggerSupport.apply[UserTask]
  import swaggerSupport._

  private val prefixPath = "/users"

  val api = new RhoRoutes[UserTask] {
    val user = "users" @@ GET / prefixPath

    "Get all users" **
      List("WIP", "dev") @@
        user |>> { () =>
      getAll.foldM(err => NotFound(), users => Ok(users))
    }

    "Find user with user id" **
      user / pathVar[Int]("id", "user Id") |>> { (userId: Int) =>
      get(userId).foldM(err => NotFound(), user => Ok(user))
    }

    "Delete user with user id" **
      DELETE / prefixPath / pathVar[Int]("id", "user Id") |>> { (userId: Int) =>
      delete(userId).foldM(err => NotFound(), user => Ok(user))
    }

    "Create a new user" **
      POST / prefixPath ^ EntityDecoder[UserTask, User] |>> { (body: User) =>
      create(body).foldM(err => InternalServerError(), _ => Created(body))
    }

    "Update user with user id" **
      PUT / prefixPath / pathVar[Int]("id", "user Id") ^ EntityDecoder[UserTask, User] |>> {
      (userId: Int, body: User) =>
        update(body).foldM(err => NotFound(), user => Ok(user))
    }
  }
}
