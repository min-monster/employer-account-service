package io.monster.ecomm.account.model

import doobie.quill.DoobieContext
import io.getquill.{idiom => _, _}

final case class User(id: Long, name: String)

final case class UserNotFound(id: Long) extends Exception

final case class AccountNotFound(id: String) extends Exception

final case class Account(
                          id: String,
                          name: String,
                          contactId: Option[String],
                          zuoraId: Option[String],
                          crmId: Option[String],
                          website: Option[String],
                          parentAccountId: Option[String],
                          address: Option[String]
                        )

object schema {

  val dc = new DoobieContext.MySQL(Literal)

  import dc._

  val account = quote {
    querySchema[Account]("ACCOUNT",
      _.contactId -> "contact_id",
      _.zuoraId -> "zuora_id",
      _.crmId -> "crm_id",
      _.parentAccountId -> "parent_account_id"
    )
  }

  val user = quote {
    querySchema[User](
      "USER"
    )
  }
}