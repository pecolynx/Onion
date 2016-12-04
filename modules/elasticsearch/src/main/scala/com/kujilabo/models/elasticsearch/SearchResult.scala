package com.kujilabo.models.elasticsearch

/**
  * 検索結果クラス
  *
  * @param hitList    検索ヒット一覧
  * @param totalCount 検索ヒット総件数
  */
class SearchResult
(
  val hitList: List[Hit],
  val totalCount: Long
) {

}
