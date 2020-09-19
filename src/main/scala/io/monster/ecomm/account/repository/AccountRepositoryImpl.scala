package io.monster.ecomm.account.repository

import doobie.Transactor
import doobie.implicits._
import io.getquill.{idiom => _}
import io.monster.ecomm.account.model.schema._
import io.monster.ecomm.account.model.{Account, AccountNotFound}
import io.monster.ecomm.account.repository.Repository.AccountService
import zio.console.Console
import zio.interop.catz._
import zio.{Task, ZIO}

final case class AccountRepositoryImpl(xa: Transactor[Task], console: Console.Service) extends AccountService {
  def getAccount(id: String): Task[Account] = {
    console.putStrLn(s"Get the account for account id: $id") *>
      SQL.getAccount(id).transact(xa)
        .foldM(
          err => Task.fail(err),
          {
            case Nil => Task.fail(AccountNotFound("1"))
            case accounts => Task.succeed(accounts.last)
          }
        )
  }

  def getAllAccounts: Task[List[Account]] = {
    SQL.getAllAccounts().transact(xa).foldM(err => Task.fail(err), output => Task.succeed(output))
  }

  def createAccount(account: Account): Task[Account] = {
    getAccount(account.id).flatMap(
      account => ZIO.effectTotal(println("There is a duplicate, so not inserting")) *> Task.succeed(account)
    ).orElse(
      SQL.createAccount(account).transact(xa).foldM(err => Task.fail(err), _ => Task.succeed(account))
    )
  }

  def deleteAccount(id: String): Task[Boolean] =
    SQL
      .deleteAccount(id)
      .transact(xa)
      .fold(_ => false, _ => true)

  def updateAccount(account: Account): Task[Boolean] = {
    getAccount(account.id).flatMap(
      account1 => Task.succeed(println(s"Updating account ${account1} to ${account}")) *>
        SQL
          .updateAccount(account).transact(xa)
          .fold(_ => false, _ => true)
    ).orElse(
      Task.fail(AccountNotFound(account.id))
    )
  }


  object SQL {

    import dc._

    // sql"""SELECT  id, name, contact_id, zuora_id, crm_id, website, parent_account_id, address FROM ACCOUNT""".queryWithLogHandler[Account](LogHandler.jdkLogHandler)
    def getAllAccounts() = run(quote {
      account
    })

    // sql"""SELECT  id, name, contact_id, zuora_id, crm_id, website, parent_account_id, address FROM ACCOUNT where id=${id}""".queryWithLogHandler[Account](LogHandler.jdkLogHandler)
    def getAccount(id: String) = run(quote {
      account.filter(_.id == lift(id))
    })

    // sql"""INSERT INTO ACCOUNT (id, name, contact_id, zuora_id, crm_id, website, parent_account_id, address) VALUES
    // (${account.id}, ${account.name}, ${account.contactId}, ${account.zuoraId}, ${account.crmId}, ${account.website}, ${account.parentAccountId}, ${account.address})
    // """.updateWithLogHandler(LogHandler.jdkLogHandler)
    def createAccount(acc: Account) = run(quote {
      account.insert(lift(acc))
    })

    // sql"""DELETE FROM ACCOUNT WHERE id = $id""".update
    def deleteAccount(id: String) = run(quote {
      account.filter(_.id == lift(id)).delete
    })

    //sql"""UPDATE ACCOUNT SET name = ${account.name} WHERE id = ${account.id}""".updateWithLogHandler(LogHandler.jdkLogHandler)
    def updateAccount(acc: Account) = run(quote {
      account.filter(_.id == lift(acc.id)).update(_.name -> lift(acc.name))
    })
  }

}







