package com.kujilabo.models.elasticsearch

import com.kujilabo.models.core._
import com.kujilabo.common._
import com.kujilabo.config.CommonConstants

/**
  * インデックス。
  *
  * @param id      モデルId
  * @param version バージョン
  * @param name    インデックス名
  */
class Index
(
  val id: ModelIdT[Int],
  val version: Int,
  val name: IndexName
) {

  /**
    * コンストラクタ。
    *
    * @param name インデックス名
    */
  def this(name: IndexName) = {
    this(CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION, name)
  }
}
