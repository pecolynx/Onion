package com.kujilabo.models.elasticsearch

import com.kujilabo.models.elasticsearch.es.EsDocument

/**
  * 検索ヒット。
  *
  * @param document ドキュメント
  * @param title    タイトル
  * @param score    スコア
  */
class Hit
(
  val document: EsDocument,
  val title: String,
  val score: Double
) {
}
