package models.elasticsearch.db

import config.Constants
import com.kujilabo.common._
import com.kujilabo.models.core._
import org.hibernate.validator.constraints.NotEmpty
import org.joda.time.DateTime
import utils.JsonUtils

import scala.annotation.meta.field

/**
  * フィールド多言語クラス。
  *
  * @param id        モデルId
  * @param version   バージョン
  * @param createdAt 作成日時
  * @param updatedAt 更新日時
  * @param coreId    コアId
  * @param langId    言語Id
  * @param name      多言語
  */
class DbFieldMultiLang
(
  val id: ModelIdT[Int],
  val version: Int,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val coreId: ModelIdT[Int],
  val langId: ModelIdT[Int],
  @(NotEmpty@field)
  val name: String
) extends AppModelT[Int](id, version, createdAt, updatedAt) {

  this.validate

  /**
    * コンストラクタ。
    *
    * @param coreId コアId
    * @param langId 言語Id
    * @param name   多言語
    */
  def this(coreId: ModelIdT[Int], langId: ModelIdT[Int], name: String) = {
    this(
      CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION,
      new DateTime(), new DateTime(),
      coreId,
      langId,
      name
    )
  }

  override def toString(): String = {
    JsonUtils.toJson(this)
  }
}