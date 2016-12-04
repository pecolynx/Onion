package service

import com.kujilabo.common.ModelNotFoundException
import com.typesafe.scalalogging.LazyLogging
import models.core._
import org.joda.time.DateTime
import scalikejdbc._
import services.AppUserService._

import scala.util.Try
import com.kujilabo.models.core._

object FileInfoService extends SQLSyntaxSupport[FileInfo] with LazyLogging {
  override val tableName = "file_info_list"
  override val autoSession = AutoSession

  override val columns = Seq(
    "app_user_id", "client_id", "file_path", "file_hash",
    "document_id", "document_size", "removed",
    "created_at", "updated_at", "removed_at"
  )


  val fi = FileInfoService.syntax("fi")

  def apply(fp: SyntaxProvider[FileInfo])(rs: WrappedResultSet): FileInfo = apply(fp.resultName)(rs)

  def apply(fi: ResultName[FileInfo])(rs: WrappedResultSet): FileInfo = new FileInfo(
    appUserId = new ModelIdImplT[Int](rs.get(fi.appUserId)),
    clientId = rs.get(fi.clientId),
    filePath = rs.get(fi.filePath),
    documentId = new ModelIdImplT[String](rs.get(fi.documentId)),
    documentSize = rs.get(fi.documentSize),
    fileHash = rs.get(fi.fileHash),
    removed = (rs.int(fi.removed) == 1),
    createdAt = rs.get(fi.createdAt),
    updatedAt = rs.get(fi.updatedAt),
    removedAt = rs.get(fi.removedAt)
  )

  /**
    * キーともとにモデルを取得します。
    *
    * @param session セッション
    * @return モデル
    */
  def getModelByKey(appUserId: ModelIdT[Int], clientId: String, filePath: String)
                   (implicit session: DBSession = autoSession): Option[FileInfo] = {
    val option = this.getModelByKeyInternal(appUserId, clientId, filePath)
    if (option.isEmpty) {
      // throw new ModelNotFoundException("{ loginId : " + appUserId + " }")
      logger.warn("{ loginId : " + appUserId + " }")
    }

    return option
  }

  /**
    * モデル一覧を取得します。
    *
    * @param page    ページ
    * @param session セッション
    * @return モデル一覧
    */
  def getModelList(appUserId: ModelIdT[Int], clientId: String, page: Page)(implicit session:
  DBSession = autoSession): List[FileInfo] = {
    withSQL {
      select.from(FileInfoService as fi).
        where.eq(fi.appUserId, appUserId.value).
        and.eq(fi.clientId, clientId).
        orderBy(fi.filePath).limit(page.size).offset((page.page -
        1) * page.size)
    }.map(FileInfoService(fi.resultName)).list.apply()
  }

  def addModel(fileInfo: FileInfo)(implicit session: DBSession = autoSession): Try[Unit] = Try {
    withSQL {
      insert.into(FileInfoService).namedValues(
        column.appUserId -> fileInfo.appUserId.value,
        column.clientId -> fileInfo.clientId,
        column.filePath -> fileInfo.filePath,
        column.fileHash -> fileInfo.fileHash,
        column.documentId -> fileInfo.documentId.value,
        column.documentSize -> fileInfo.documentSize,
        column.removed -> false,
        column.createdAt -> new DateTime(),
        column.updatedAt -> new DateTime(),
        column.removedAt -> null
      )
    }.update().apply()
  }

  def removeModel(appUserId: ModelIdT[Int], clientId: String, filePath: String)
                 (implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      update(FileInfoService).set(
        column.removed -> true,
        column.removedAt -> new DateTime()
      ).where.eq(column.appUserId, appUserId.value).
        and.eq(column.clientId, clientId).
        and.eq(column.filePath, filePath)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + appUserId.value)
    }
  }

  def removeModelByUser(appUserId: ModelIdT[Int], clientId: String)
                       (implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      delete.from(FileInfoService).
        where.eq(column.appUserId, appUserId.value).
        and.eq(column.clientId, clientId)
    }.update.apply()

    if (count != 1) {
      // throw new ModelNotFoundException("id : " + appUserId.value)
    }
  }

  def removeAll()(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(FileInfoService)
    }.update.apply()
  }

  def updateModel(entity: FileInfo)(implicit session: DBSession = autoSession): Unit = {
    val removed = if (entity.removed) 1 else 0
    val count = withSQL {
      update(FileInfoService).set(
        column.appUserId -> entity.appUserId.value,
        column.clientId -> entity.clientId,
        column.filePath -> entity.filePath,
        column.documentId -> entity.documentId.value,
        column.documentSize -> entity.documentSize,
        column.fileHash -> entity.fileHash,
        column.removed -> removed,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> new DateTime()
      ).where.eq(column.appUserId, entity.appUserId.value).
        and.eq(column.clientId, entity.clientId).
        and.eq(column.filePath, entity.filePath)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + entity.appUserId.value)
    }
  }

  def containsModel(appUserId: ModelIdT[Int], clientId: String, filePath: String)
                   (implicit session: DBSession = autoSession): Boolean = {
    return this.getModelByKeyInternal(appUserId, clientId, filePath).isDefined
  }

  private def getModelByKeyInternal
  (
    appUserId: ModelIdT[Int], clientId: String, filePath: String
  )(implicit session: DBSession = autoSession): Option[FileInfo] = {
    withSQL {
      select.from(FileInfoService as fi).
        where.eq(fi.appUserId, appUserId.value).
        and.eq(fi.clientId, clientId).
        and.eq(fi.filePath, filePath)
    }.map(FileInfoService(fi.resultName)).single.apply()
  }
}
