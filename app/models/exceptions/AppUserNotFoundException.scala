package models.exceptions

import com.kujilabo.common.ModelNotFoundException

/**
  * アプリケーションユーザが見つからない例外。
  *
  * @param message メッセージ
  */
class AppUserNotFoundException(message: String) extends ModelNotFoundException(message) {

}
