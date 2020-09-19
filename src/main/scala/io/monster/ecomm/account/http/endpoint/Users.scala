package io.monster.ecomm.account.http.endpoint

import io.circe.generic.auto._
import io.circe.{Decoder, Encoder}
import io.monster.ecomm.account.environment.Environments.AppEnvironment
import io.monster.ecomm.account.model.User
import io.monster.ecomm.account.repository._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import zio.RIO
import zio.interop.catz._

object Users {
  type UserTask[A] = RIO[AppEnvironment, A]

  private val prefixPath = "/users"

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[UserTask, A] = jsonOf[UserTask, A]

  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[UserTask, A] = jsonEncoderOf[UserTask, A]

  val dsl: Http4sDsl[UserTask] = Http4sDsl[UserTask]

  import dsl._

  val userRoutes =
    HttpRoutes.of[UserTask] {
      case GET -> Root =>
        Ok(getAll)
      case GET -> Root / IntVar(id) =>
        Ok(get(id))
      case request@POST -> Root =>
        request.decode[User] {
          user => Created(create(user))
        }
      case DELETE -> Root / IntVar(id) => Ok(delete(id))
      case request@PUT -> Root / IntVar(id) =>
        request.decode[User] {
          user => Ok(update(user))
        }
    }

  val routes: HttpRoutes[UserTask] = Router(
    prefixPath -> userRoutes
  )
}
