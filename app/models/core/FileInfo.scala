package models.core

import org.joda.time.DateTime

/**
  * ファイル情報。
  *
  * @param appUserId    登録者
  * @param clientId     クライアントId
  * @param filePath     ファイルパス
  * @param documentId   ドキュメントId
  * @param documentSize ドキュメントサイズ
  * @param fileHash     ファイルハッシュ
  * @param removed      削除済みフラグ
  * @param createdAt    ファイル作成日時
  * @param updatedAt    ファイル更新日時
  * @param removedAt    ファイル削除日時
  */
class FileInfo
(
  val appUserId: ModelIdT[Int],
  val clientId: String,
  val filePath: String,
  val documentId: ModelIdT[String],
  val documentSize: Int,
  val fileHash: String,
  val removed: Boolean,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val removedAt: DateTime
) extends BaseObject {

}
