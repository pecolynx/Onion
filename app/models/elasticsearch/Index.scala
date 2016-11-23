package models.elasticsearch

import config.Constants
import models.core.{ModelIdImplT, ModelIdT}

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
    this(Constants.DEFAULT_ID, Constants.DEFAULT_VERSION, name)
  }
}
