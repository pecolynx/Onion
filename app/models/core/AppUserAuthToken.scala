package models.core

import org.joda.time.DateTime

/**
  * アプリケーションユーザートークン。
  *
  * @param appUserId アプリケーションユーザーId
  * @param authToken 認証トークン
  * @param expires   有効期限
  */
class AppUserAuthToken
(
  val appUserId: ModelIdT[Int],
  val authToken: String,
  val expires: DateTime
) {

}
