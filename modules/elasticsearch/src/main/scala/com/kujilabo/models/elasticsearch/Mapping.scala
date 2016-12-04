package com.kujilabo.models.elasticsearch

import com.kujilabo.models.core._
import com.kujilabo.config.CommonConstants

/**
  * マッピング
  *
  * @param id      モデルId
  * @param version バージョン
  * @param index   インデックス
  * @param name    マッピング名
  */
class Mapping
(
  val id: ModelIdT[Int],
  val version: Int,
  val index: Index,
  val name: MappingName
) extends BaseObject {

  this.validate()

  /**
    * コンストラクタ。
    *
    * @param index インデックス
    * @param name  マッピング名
    */
  def this(index: Index, name: MappingName) = {
    this(CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION, index, name)
  }
}
