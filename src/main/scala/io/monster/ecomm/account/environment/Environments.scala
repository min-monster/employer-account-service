package io.monster.ecomm.account.environment

import io.monster.ecomm.account.configuration.Configuration
import io.monster.ecomm.account.repository.{AccountRepository, DbTransactor, Repository, UserRepository}
import zio.ULayer
import zio.clock.Clock
import zio.console.Console

object Environments {
  type HttpServerEnvironment = Configuration with Clock
  type AppEnvironment = HttpServerEnvironment with UserRepository with AccountRepository

  val httpServerEnvironmentLayer: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
  val dbTransactorLayer: ULayer[DbTransactor] = Configuration.live >>> DbTransactor.db
  val usersRepositoryLayer: ULayer[UserRepository] = dbTransactorLayer >>> Repository.live
  val dbTransactorWithConsole: ULayer[DbTransactor with Console] = dbTransactorLayer ++ Console.live
  val accountRepositoryLayer: ULayer[AccountRepository] = dbTransactorWithConsole >>> Repository.accountLive
  val appEnvironmentLayer: ULayer[AppEnvironment] = httpServerEnvironmentLayer ++ usersRepositoryLayer ++ accountRepositoryLayer
}