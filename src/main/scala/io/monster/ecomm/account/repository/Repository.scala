package io.monster.ecomm.account.repository

import io.monster.ecomm.account.model.{ Account, User }
import zio.console.Console
import zio.{ Task, URLayer, ZLayer }

object Repository {

  trait UserService {
    def get(id: Long): Task[User]

    def getAll: Task[List[User]]

    def create(user: User): Task[User]

    def delete(id: Long): Task[Boolean]

    def update(user: User): Task[Boolean]
  }

  trait AccountService {
    def getAccount(id: String): Task[Account]

    def getAllAccounts: Task[List[Account]]

    def createAccount(account: Account): Task[Account]

    def deleteAccount(id: String): Task[Boolean]

    def updateAccount(account: Account): Task[Boolean]
  }

  val live: URLayer[DbTransactor, UserRepository] =
    /* ZLayer.fromService { resource => UserRepositoryImpl(resource.xa) } */
    ZLayer.fromFunction { resource =>
      UserRepositoryImpl(resource.get.xa)
    }

  val accountLive: URLayer[DbTransactor with Console, AccountRepository] =
    ZLayer.fromFunction { resource =>
      AccountRepositoryImpl(resource.get[DbTransactor.Resource].xa, resource.get[Console.Service])
    }
}
