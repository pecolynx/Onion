package com.kujilabo.util

/**
  * 文字列ユーティリティ。
  */
object StringUtils {
  def isBlank(value: String): Boolean = {
    if (value == null) {
      return true
    }
    else if (value == "") {
      return true
    }

    return false
  }
}
