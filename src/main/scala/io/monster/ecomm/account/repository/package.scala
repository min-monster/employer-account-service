package io.monster.ecomm.account

import io.monster.ecomm.account.configuration.Configuration.DbConfig
import io.monster.ecomm.account.model.{ Account, User }
import zio.interop.catz._
import zio.{ Has, RIO }

package object repository {
  type UserRepository = Has[Repository.UserService]
  type AccountRepository = Has[Repository.AccountService]
  type DbTransactor = Has[DbTransactor.Resource]
  type DBConfig = Has[DbConfig]

  def get(id: Int): RIO[UserRepository, User] = RIO.accessM(_.get.get(id))

  def getAll: RIO[UserRepository, List[User]] = RIO.accessM(_.get.getAll)

  def create(user: User): RIO[UserRepository, User] =
    RIO.accessM(_.get.create(user))

  def delete(id: Int): RIO[UserRepository, Boolean] =
    RIO.accessM(_.get.delete(id))

  def update(user: User): RIO[UserRepository, Boolean] =
    RIO.accessM(_.get.update(user))

  def getAccount(id: String): RIO[AccountRepository, Account] =
    RIO.accessM(_.get.getAccount(id))

  def getAllAccounts: RIO[AccountRepository, List[Account]] =
    RIO.accessM(_.get.getAllAccounts)

  def createAccount(account: Account): RIO[AccountRepository, Account] =
    RIO.accessM(_.get.createAccount(account))

  def deleteAccount(id: String): RIO[AccountRepository, Boolean] =
    RIO.accessM(_.get.deleteAccount(id))

  def updateAccount(account: Account): RIO[AccountRepository, Boolean] =
    RIO.accessM(_.get.updateAccount(account))

}
