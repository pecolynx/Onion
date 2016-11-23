package models.core

import config.Constants
import org.joda.time.DateTime

/**
  * モデルクラス
  *
  * @param id        モデルId
  * @param version   バージョン
  * @param createAt  作成日時
  * @param updatedAt 更新日時
  * @tparam T モデルIdの型
  */
class AppModelT[T]
(
  id: ModelIdT[Int],
  version: Int,
  val createAt: DateTime,
  updatedAt: DateTime
) extends BaseObject {

  /**
    * コンストラクタ。
    */
  def this() = {
    this(
      Constants.DEFAULT_ID,
      Constants.DEFAULT_VERSION,
      new DateTime(),
      new DateTime()
    )
  }
}
