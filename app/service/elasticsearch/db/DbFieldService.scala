package service.elasticsearch.db

import models.core._
import models.elasticsearch.db.{DbField, DbFieldMultiLang, DbIndex}
import models.exceptions.ModelNotFoundException
import org.joda.time.DateTime
import scalikejdbc._
import com.kujilabo.common._
import com.kujilabo.models.core._

object DbFieldService extends SQLSyntaxSupport[DbField] {

  override val tableName = "field_core_list"
  override val autoSession = AutoSession

  override val columns = Seq(
    "ID", "VERSION", "CREATED_AT", "UPDATED_AT",
    "NAME", "FIELD_TYPE", "SEQ", "MAPPING_ID"
  )

  def apply(df: SyntaxProvider[DbField], dfm: SyntaxProvider[DbFieldMultiLang])(rs: WrappedResultSet): DbField = {
    apply(df.resultName, dfm.resultName)(rs)
  }

  def apply(df: ResultName[DbField])(rs: WrappedResultSet): DbField = {
    new DbField(
      id = new ModelIdImplT[Int](rs.get(df.id)),
      version = rs.get(df.version),
      createdAt = rs.get(df.createdAt),
      updatedAt = rs.get(df.updatedAt),
      name = new VariableName(rs.get(df.name)),
      fieldType = rs.get(df.fieldType),
      seq = rs.get(df.seq),
      mappingId = new ModelIdImplT[Int](rs.get(df.mappingId)),
      multiLangName = null
    )
  }

  def apply(df: ResultName[DbField], dfm: ResultName[DbFieldMultiLang])(rs: WrappedResultSet): DbField = {
    new DbField(
      id = new ModelIdImplT[Int](rs.get(df.id)),
      version = rs.get(df.version),
      createdAt = rs.get(df.createdAt),
      updatedAt = rs.get(df.updatedAt),
      name = new VariableName(rs.get(df.name)),
      fieldType = rs.get(df.fieldType),
      seq = rs.get(df.seq),
      mappingId = new ModelIdImplT[Int](rs.get(df.mappingId)),
      multiLangName = rs.get(dfm.name)
    )
  }

  def getModelByKey(mappingId: ModelIdT[Int], name: VariableName)(implicit session: DBSession = autoSession): DbField = {
    val option = withSQL {
      select.from(DbFieldService as df).where.append(
        sqls.eq(df.mappingId, mappingId.value).and.eq(df.name, name.value))
    }.map(DbFieldService(df.resultName)).single.apply()

    if (option.isEmpty) {
      throw new ModelNotFoundException("{ mappingId : " + mappingId + "}")
    }

    return option.get
  }

  def addModel(entity: DbField, langId: ModelIdT[Int])(implicit session: DBSession =
  autoSession):
  Unit
  = {
    DB.localTx { implicit session =>
      val generatedKey = withSQL {
        insert.into(DbFieldService).namedValues(
          column.version -> entity.version,
          column.createdAt -> entity.createdAt,
          column.updatedAt -> entity.updatedAt,
          column.name -> entity.name.value,
          column.fieldType -> entity.fieldType,
          column.seq -> entity.seq,
          column.mappingId -> entity.mappingId.value
        )
      }.updateAndReturnGeneratedKey.apply()

      DbFieldMultiLangService.addModel(
        new DbFieldMultiLang(
          new ModelIdImplT[Int](generatedKey.toInt),
          langId,
          entity.multiLangName
        )
      )
    }
  }

  val (df, dfm) = (DbFieldService.syntax, DbFieldMultiLangService.syntax)

  def getModelList(mappingId: ModelIdT[Int])(implicit session: DBSession = autoSession): List[DbField] = {
    val groups =
      sql"""
  select
    ${
        df.result.*
      }, ${
        dfm.result.*
      }
  from
    ${
        DbFieldService.as(df)
      } left join ${
        DbFieldMultiLangService.as(dfm)
      } on ${
        df.id
      } = ${
        dfm.coreId
      }
  where
    ${
        df.mappingId
      } = ${
        mappingId.value
      }
  order by
    ${
        df.seq
      }
"""
        .map(DbFieldService(df.resultName, dfm.resultName))
    //          .toMany(DbFieldMultiLangService.opt(dfm))
    //          .map { (core, multiLangList) => core.copy(multiLangList = multiLangList) }

    val a = groups.list()
    println("a = " + a)
    val b = a.apply
    return b
    //return List.empty[DbField]
  }

  def removeAll()(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(DbFieldService)
    }.update.apply()
  }

  def updateModel(entity: DbField)(implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      update(DbFieldService).set(
        column.version -> (entity.version + 1),
        column.updatedAt -> new DateTime(),
        column.multiLangName -> entity.multiLangName,
        column.seq -> entity.seq
      ).where.eq(column.id, entity.id.value).and.eq(column.version, entity.version)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + entity.id.value)
    }
  }

  def contains(mappingId: ModelIdT[Int], name: VariableName)(implicit session: DBSession = autoSession): Boolean = {
    this.getModelByKeyInternal(mappingId, name).isDefined
  }

  private def getModelByKeyInternal(mappingId: ModelIdT[Int], name: VariableName)
                                   (implicit session: DBSession = autoSession): Option[DbField] = {
    withSQL {
      select.from(DbFieldService as df).where.append(
        sqls.eq(df.mappingId, mappingId.value).and.eq(df.name, name.value))
    }.map(DbFieldService(df.resultName)).single.apply()
  }
}

