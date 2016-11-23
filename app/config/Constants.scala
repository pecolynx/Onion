package config

import models.core.{ModelIdImplT, ModelIdT}

object Constants {
  val VERSION: String = "1.0"
  val DEFAULT_ID: ModelIdT[Int] = new ModelIdImplT[Int](0)
  val DEFAULT_ID_STRING: ModelIdT[String] = new ModelIdImplT[String]("")
  val DEFAULT_VERSION: Int = 1
  val LANG_ID_JA = 1
  final val LOGIN_ID_LENGTH_MIN = 1
  final val LOGIN_ID_LENGTH_MAX = 20
  final val AUTH_TOKEN_LENGTH_MIN = 1
  final val AUTH_TOKEN_LENGTH_MAX = 20
  final val PAGE_SIZE_VALUE_MIN = 1
  final val PAGE_SIZE_VALUE_MAX = 1000
  final val PAGE_NUMBER_VALUE_MIN = 1
  final val HIT_RESULT_CONTENT_MAX_LENGTH = 1024
}
