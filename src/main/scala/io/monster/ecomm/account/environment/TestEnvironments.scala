package io.monster.ecomm.account.environment

import io.monster.ecomm.account.configuration.Configuration
import io.monster.ecomm.account.environment.Environments.{ AppEnvironment, HttpServerEnvironment }
import io.monster.ecomm.account.repository.{ AccountRepository, DbTransactor, Repository, UserRepository }
import zio.ULayer
import zio.clock.Clock
import zio.console.Console

object TestEnvironments {
  val httpServerEnvironmentLayer: ULayer[HttpServerEnvironment] =
    Configuration.live ++ Clock.live
  val dbTransactorLayer: ULayer[DbTransactor] =
    Configuration.test >>> DbTransactor.db
  val usersRepositoryLayer: ULayer[UserRepository] =
    dbTransactorLayer >>> Repository.live
  val dbTransactorWithConsole: ULayer[DbTransactor with Console] =
    dbTransactorLayer ++ Console.live
  val accountRepositoryLayer: ULayer[AccountRepository] =
    (dbTransactorLayer ++ Console.live) >>> Repository.accountLive
  val appEnvironmentLayer: ULayer[AppEnvironment] =
    httpServerEnvironmentLayer ++ usersRepositoryLayer ++ accountRepositoryLayer
}
