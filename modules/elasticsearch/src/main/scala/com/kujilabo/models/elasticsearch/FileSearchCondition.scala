package com.kujilabo.models.elasticsearch

import com.kujilabo.models.core.Page

/**
  * 検索条件。
  *
  * @param page        ページ
  * @param keywordList 検索キーワード一覧
  * @param extList     拡張子一覧
  */
class FileSearchCondition
(
  val page: Page,
  val keywordList: List[String],
  val extList: List[String]
) {

}
