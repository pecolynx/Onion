package com.kujilabo.models.core

import com.kujilabo.common.CommonConstants
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
      CommonConstants.DEFAULT_ID,
      CommonConstants.DEFAULT_VERSION,
      new DateTime(),
      new DateTime()
    )
  }
}
