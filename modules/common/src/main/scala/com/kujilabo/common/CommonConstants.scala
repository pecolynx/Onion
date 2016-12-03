package com.kujilabo.common

import com.kujilabo.models.core.{ModelIdImplT, ModelIdT}

object CommonConstants {
  val DEFAULT_ID: ModelIdT[Int] = new ModelIdImplT[Int](0)
  val DEFAULT_ID_STRING: ModelIdT[String] = new ModelIdImplT[String]("")
  val DEFAULT_VERSION: Int = 1
}
