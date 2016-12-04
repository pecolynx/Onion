package com.kujilabo.service.elasticsearch.db

import com.kujilabo.common.ModelNotFoundException
import com.typesafe.scalalogging.LazyLogging
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.MappingName
import com.kujilabo.models.elasticsearch.db.DbMapping
import org.joda.time.DateTime
import scalikejdbc.{DBSession, WrappedResultSet, _}


object DbMappingService extends SQLSyntaxSupport[DbMapping] with LazyLogging {
  override val tableName = "mapping_list"
  override val autoSession = AutoSession

  override val columns = Seq(
    "ID", "VERSION", "CREATED_AT", "UPDATED_AT",
    "INDEX_ID", "NAME"
  )

  val dbm = DbMappingService.syntax("dbm")

  def apply(dbm: SyntaxProvider[DbMapping])(rs: WrappedResultSet): DbMapping = apply(dbm.resultName)(rs)

  def apply(dbm: ResultName[DbMapping])(rs: WrappedResultSet): DbMapping = {
    new DbMapping(
      id = new ModelIdImplT[Int](rs.get(dbm.id)),
      version = rs.get(dbm.version),
      createdAt = rs.get(dbm.createdAt),
      updatedAt = rs.get(dbm.updatedAt),
      indexId = new ModelIdImplT[Int](rs.get(dbm.indexId)),
      name = new MappingName(new VariableName(rs.get(dbm.name))))
  }

  /**
    * キーともとにモデルを取得します。
    *
    * @param mappingName ログインId
    * @param session     セッション
    * @return モデル
    */
  def getModelByKey(indexId: ModelIdT[Int], mappingName: MappingName)
                   (implicit session: DBSession = autoSession): DbMapping = {
    val option = getModelByKeyInternal(indexId, mappingName)
    if (option.isEmpty) {
      throw new ModelNotFoundException(indexId.toString)
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
  def getModelList(page: Page)(implicit session: DBSession = autoSession): List[DbMapping] = {
    withSQL {
      select.from(DbMappingService as dbm).orderBy(dbm.id).limit(page.size).offset((page.page - 1) * page.size)
    }.map(DbMappingService(dbm.resultName)).list.apply()
  }

  def addModel(entity: DbMapping)(implicit session: DBSession = autoSession): Unit = {
    val generatedKey = withSQL {
      insert.into(DbMappingService).namedValues(
        column.version -> entity.version,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt,
        column.indexId -> entity.indexId.value,
        column.name -> entity.name.value.value
      )
    }.updateAndReturnGeneratedKey.apply()
  }

  def removeModel(id: ModelIdT[Int], version: Int)(implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      delete.from(DbMappingService).where.eq(column.id, id.value).and.eq(column.version, version)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + id.value)
    }
  }

  def removeAll()(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(DbMappingService)
    }.update.apply()
  }

  def updateModel(entity: DbMapping)(implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      update(DbMappingService).set(
        column.version -> (entity.version + 1),
        column.updatedAt -> new DateTime(),
        column.indexId -> entity.indexId.value,
        column.name -> entity.name.value.value
      ).where.eq(column.id, entity.id.value).and.eq(column.version, entity.version)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + entity.id.value)
    }
  }

  def containsModel(indexId: ModelIdT[Int], mappingName: MappingName): Boolean = {
    val model = this.getModelByKeyInternal(indexId, mappingName)
    model.isDefined
  }


  private def getModelByKeyInternal(indexId: ModelIdT[Int], mappingName: MappingName)
                                   (implicit session: DBSession = autoSession): Option[DbMapping] = {
    withSQL {
      select.from(DbMappingService as dbm).where.append(sqls.eq(dbm.name, mappingName.value.value))
    }.map(DbMappingService(dbm.resultName)).single.apply()
  }
}
