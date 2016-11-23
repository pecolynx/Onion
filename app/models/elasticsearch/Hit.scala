package models.elasticsearch

import models.core.Title
import models.elasticsearch.es.EsDocument
import viewmodels.VmHit

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
