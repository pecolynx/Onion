package models.elasticsearch

import config.Constants
import models.core.{BaseObject, ModelIdImplT, ModelIdT}

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
    this(Constants.DEFAULT_ID, Constants.DEFAULT_VERSION, index, name)
  }
}
