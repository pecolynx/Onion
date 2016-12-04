package com.kujilabo.service.elasticsearch.db

import org.joda.time.DateTime
import scalikejdbc._
import scala.util.Try
import com.kujilabo.models.core.AppModelT
import com.kujilabo.models.core.ModelIdT
import com.kujilabo.models.core._
import com.kujilabo.common._
import com.kujilabo.models.elasticsearch.IndexName
import com.kujilabo.models.elasticsearch.db.DbIndex

object DbIndexService extends SQLSyntaxSupport[DbIndex] {
  override val tableName = "index_list"
  override val autoSession = AutoSession
  //
  //  override val columns = Seq(
  //    "ID", "VERSION", "CREATED_AT", "UPDATED_AT",
  //    "NAME"
  //  )

  val i = DbIndexService.syntax("i")

  def apply(i: SyntaxProvider[DbIndex])(rs: WrappedResultSet): DbIndex = apply(i.resultName)(rs)

  def apply(i: ResultName[DbIndex])(rs: WrappedResultSet): DbIndex = {
    new DbIndex(
      id = new ModelIdImplT[Int](rs.get(i.id)),
      version = rs.get(i.version),
      createdAt = rs.get(i.createdAt),
      updatedAt = rs.get(i.updatedAt),
      name = new IndexName(new VariableName(rs.get(i.name)))
    )
  }

  /**
    * キーともとにモデルを取得します。
    *
    * @param name    ログインId
    * @param session セッション
    * @return モデル
    */
  def getModelByKey(name: IndexName)(implicit session: DBSession = autoSession): DbIndex = {
    val option = this.getModelByKeyInternal(name)
    if (option.isEmpty) {
      throw new ModelNotFoundException(name.toString())
    }

    return option.get
  }

  /**
    * モデル一覧を取得します。
    *
    * @param page    ページ
    * @param session セッション
    * @return モデル一覧
    */
  def getModelList(page: Page)(implicit session: DBSession = autoSession): List[DbIndex] = {
    withSQL {
      select.from(DbIndexService as i).orderBy(i.id).limit(page.size).offset((page.page - 1) *
        page.size)
    }.map(DbIndexService(i.resultName)).list.apply()
  }

  def addModel(entity: DbIndex)(implicit session: DBSession = autoSession): Try[Unit] = Try {
    val generatedKey = withSQL {
      insert.into(DbIndexService).namedValues(
        column.version -> entity.version,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt,
        column.name -> entity.name.value.value
      )
    }.updateAndReturnGeneratedKey.apply()
  }

  def removeModel(id: ModelIdT[Int], version: Int)
                 (implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      delete.from(DbIndexService).where.eq(column.id, id.value).and.eq(column.version, version)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + id.value)
    }
  }

  def removeAll()(implicit session: DBSession = autoSession): Unit = {
    val y = delete.from(DbIndexService)
    withSQL {
      y
    }.update.apply()
  }

  def updateModel(entity: DbIndex)(implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      update(DbIndexService).set(
        column.version -> (entity.version + 1),
        column.updatedAt -> new DateTime(),
        column.name -> entity.name.value.value
      ).where.eq(column.id, entity.id.value).and.eq(column.version, entity.version)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + entity.id.value)
    }
  }

  def containsModel(indexName: IndexName)(implicit session: DBSession = autoSession): Boolean = {
    this.getModelByKeyInternal(indexName).isDefined
  }

  private def getModelByKeyInternal(name: IndexName)
                                   (implicit session: DBSession = autoSession): Option[DbIndex] = {
    withSQL {
      select.from(DbIndexService as i).where.append(sqls.eq(i.name, name.value.value))
    }.map(DbIndexService(i.resultName)).single.apply()
  }
}
