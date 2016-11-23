package models.core

import javax.validation.constraints.Size

import config.Constants
import org.joda.time.DateTime

import scala.annotation.meta.field

/**
  * アプリケーションユーザー。
  *
  * @param id            モデルId
  * @param version       バージョン
  * @param createdAt     作成日時
  * @param updatedAt     更新日時
  * @param loginId       ログインId
  * @param loginPassword ログインパスワード
  * @param email         メールアドレス
  * @param name          名前
  */
class AppUser
(
  val id: ModelIdT[Int],
  val version: Int,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  @(Size@field)(max = 20, message = "foobar")
  val loginId: String,
  @(Size@field)(max = 200)
  val loginPassword: String,
  @(Size@field)(max = 40)
  val email: String,
  @(Size@field)(max = 40)
  val name: String
) extends AppModelT[Int](id, version, createdAt, updatedAt) {

  println("login_password = " + loginPassword)
  this.validate

  /**
    * コンストラクタ。
    *
    * @param loginId       ログインId
    * @param loginPassword ログインパスワード
    * @param email         メールアドレス
    * @param name          名前
    */
  def this(loginId: String, loginPassword: String, email: String, name: String) = {
    this(
      Constants.DEFAULT_ID, Constants.DEFAULT_VERSION, new DateTime(), new DateTime(),
      loginId,
      loginPassword,
      email,
      name
    )
  }
}
