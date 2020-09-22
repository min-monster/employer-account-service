package io.monster.ecomm.account.repository

import cats.effect.Blocker
import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import doobie.hikari.HikariTransactor
import io.monster.ecomm.account.Main.platform
import zio.interop.catz.{ taskConcurrentInstance, zioContextShift }
import zio.{ Task, URLayer, ZLayer }

object DbTransactor {

  trait Resource {
    val xa: HikariTransactor[Task]
  }

  val db: URLayer[DBConfig, DbTransactor] = ZLayer.fromService { db =>
    new Resource {
      val xa: HikariTransactor[Task] = {
        val config = new HikariConfig()
        config.setJdbcUrl(db.url)
        config.setUsername(db.user)
        config.setPassword(db.password)
        config.setMaximumPoolSize(5)
        HikariTransactor.apply[Task](
          new HikariDataSource(config),
          platform.executor.asEC,
          Blocker.liftExecutionContext(platform.executor.asEC)
        )
      }
    }
  }
}
