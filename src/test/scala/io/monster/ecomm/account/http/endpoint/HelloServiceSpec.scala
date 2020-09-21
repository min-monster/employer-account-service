package io.monster.ecomm.account.http.endpoint

import org.http4s._
import org.http4s.implicits._
import zio.{Task, _}
import zio.interop.catz._
import zio.test._
import zio.test.Assertion._
import zio.test.interop.CatsTestFunctions
import zio.test.interop.catz._
import TestAspect._
import Uri._
import io.circe.{Decoder, Encoder}
import io.monster.ecomm.account.http.HelloService
import io.monster.ecomm.account.http.endpoint.Users.UserTask
import io.monster.ecomm.account.model.UserNotFound
import org.http4s.circe.{jsonEncoderOf, jsonOf}

// object HelloServiceSpec extends DefaultRunnableSpec {
//   override def spec = suite("routes suite")(
//     testM("root request returns hello!") {
//      val io =for {
//         result <- HelloService.service.run(Request[Task](Method.GET, uri"/")).value
//         _ <- ZIO.effectTotal(println(result))
//        body <- result.orNull.body.compile.toVector.map(x => x.map(_.toChar).mkString(""))
//       } yield body
//       assertM(io)(equalTo("hello!"))
//     }
//   ) @@ sequential
// }
