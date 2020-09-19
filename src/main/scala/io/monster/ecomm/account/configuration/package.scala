package io.monster.ecomm.account

import io.monster.ecomm.account.configuration.Configuration.{DbConfig, HttpServerConfig}
import zio.Has

package object configuration {
  type Configuration = Has[DbConfig] with Has[HttpServerConfig]
}
