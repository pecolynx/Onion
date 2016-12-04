package models.core

import javax.validation.constraints.Size

import config.Constants
import org.joda.time.DateTime

import scala.annotation.meta.field
import com.kujilabo.models.core._
import com.kujilabo.common._
import com.kujilabo.config.CommonConstants

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
      CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION,
      new DateTime(), new DateTime(),
      loginId,
      loginPassword,
      email,
      name
    )
  }
}
