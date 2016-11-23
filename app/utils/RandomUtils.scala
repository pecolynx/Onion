package utils

/**
  * 乱数ユーティリティ。
  */
object RandomUtils {
  def createString(length: Int): String = {
    new scala.util.Random(new java.security.SecureRandom()).alphanumeric.take(length).mkString
  }
}
