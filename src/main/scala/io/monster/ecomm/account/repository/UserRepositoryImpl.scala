package io.monster.ecomm.account.repository

import doobie.hikari.HikariTransactor
import doobie.implicits._
import io.getquill.{idiom => _}
import io.monster.ecomm.account.model.schema._
import io.monster.ecomm.account.model.{User, UserNotFound}
import io.monster.ecomm.account.repository.Repository.UserService
import zio.Task
import zio.interop.catz._

final case class UserRepositoryImpl(xa: HikariTransactor[Task]) extends UserService {
  def get(id: Long): Task[User] = {
    SQL.get(id)
      .transact(xa)
      .foldM(
        err => Task.fail(err),
        {
          case Nil => Task.fail(UserNotFound(1))
          case users => Task.succeed(users.last)
        }
      )
  }

  def getAll: Task[List[User]] = {
    SQL.getAll.transact(xa).foldM(err => Task.fail(err), output => Task.succeed(output))
  }

  def create(user: User): Task[User] = {
    get(user.id).flatMap(
      user => Task.succeed(println("There is a duplicate, so not inserting")) *> Task.succeed(user)
    ).orElse(
      SQL.create(user).transact(xa).foldM(err => Task.fail(err), _ => Task.succeed(user))
    )
  }

  def delete(id: Long): Task[Boolean] =
    SQL
      .delete(id)
      .transact(xa)
      .fold(_ => false, _ => true)


  def update(user: User): Task[Boolean] = {
    get(user.id).flatMap(
      user1 => Task.succeed(println(s"Updating user ${user1} to ${user}")) *>
        SQL
          .update(user)
          .transact(xa)
          .fold(_ => false, _ => true)
    ).orElse(
      Task.fail(UserNotFound(user.id))
    )
  }
}

object SQL {

  import dc._

  //sql"""SELECT  id, name FROM USER""".queryWithLogHandler[User](LogHandler.jdkLogHandler)
  def getAll = run(quote {
    user
  })

  // sql"""SELECT  id, name FROM USER where id=${id}""".queryWithLogHandler[User](LogHandler.jdkLogHandler)
  def get(id: Long) = run(quote {
    user.filter(_.id == lift(id))
  })

  // sql"""INSERT INTO USER (id, name) VALUES (${user.id}, ${user.name})""".updateWithLogHandler(LogHandler.jdkLogHandler)
  def create(userIn: User) = run(quote {
    user.insert(lift(userIn))
  })

  // sql"""DELETE FROM USER WHERE id = $id""".update
  def delete(id: Long) = run(quote {
    user.filter(_.id == lift(id)).delete
  })

  // sql"""UPDATE USER SET name = ${user.name} WHERE id = ${user.id}""".updateWithLogHandler(LogHandler.jdkLogHandler)
  def update(userIn: User) = run(quote {
    user.filter(_.id == lift(userIn.id)).update(_.name -> lift(userIn.name))
  })

}







