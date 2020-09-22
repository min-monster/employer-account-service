package io.monster.ecomm.account.configuration

import pureconfig.ConfigSource
import zio.{ Has, Task, ULayer, ZIO, ZLayer }
import pureconfig.generic.auto._

object Configuration {

  case class AppConfig(httpServer: HttpServerConfig, database: DbConfig)

  case class HttpServerConfig(host: String, port: Int, path: String)

  case class DbConfig(driver: String, url: String, user: String, password: String)

  val live: ULayer[Configuration] = ZLayer.fromEffectMany(
    ZIO
      .effect(ConfigSource.default.loadOrThrow[AppConfig])
      .map(c => Has(c.database) ++ Has(c.httpServer))
      .orDie
  )

  val test: ULayer[Configuration] = ZLayer.fromEffectMany(
    Task.effectTotal(
      Has(HttpServerConfig("localhost", 8080, "")) ++
        Has(
          DbConfig(
            "org.h2.Driver",
            "jdbc:h2:~/test1;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:schema.sql';",
            "",
            ""
          )
        )
    )
  )
}
