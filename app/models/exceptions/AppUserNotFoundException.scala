package models.exceptions

/**
  * アプリケーションユーザが見つからない例外。
  *
  * @param message メッセージ
  */
class AppUserNotFoundException(message: String) extends ModelNotFoundException(message) {

}
