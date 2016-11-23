package viewmodels

/**
  * 検索ヒットビューモデルクラス。
  *
  * @param document ドキュメント
  * @param title    タイトル
  * @param score    マッチスコア
  */
class VmHit
(
  val document: VmDocument,
  val title: String,
  val score: Double
) {

}
