package service.elasticsearch.db

import models.core.ModelIdImplT
import models.elasticsearch.db.DbFieldMultiLang
import scalikejdbc._


object DbFieldMultiLangService extends SQLSyntaxSupport[DbFieldMultiLang] {

  override val tableName = "field_multi_lang_list"
  override val autoSession = AutoSession

  override val columns = Seq(
    "ID", "VERSION", "CREATED_AT", "UPDATED_AT",
    "CORE_ID", "LANG_ID", "NAME"
  )

  def apply(dfm: SyntaxProvider[DbFieldMultiLang])(rs: WrappedResultSet): DbFieldMultiLang = apply(dfm.resultName)(rs)

  def apply(dfm: ResultName[DbFieldMultiLang])(rs: WrappedResultSet): DbFieldMultiLang = {
    new DbFieldMultiLang(
      id = new ModelIdImplT[Int](rs.get(dfm.id)),
      version = rs.get(dfm.version),
      createdAt = rs.get(dfm.createdAt),
      updatedAt = rs.get(dfm.updatedAt),
      coreId = new ModelIdImplT[Int](rs.get(dfm.coreId)),
      langId = new ModelIdImplT[Int](rs.get(dfm.langId)),
      name = rs.get(dfm.name)
    )

  }

  def addModel(entity: DbFieldMultiLang)(implicit session: DBSession = autoSession): Unit = {
    val generatedKey = withSQL {
      insert.into(DbFieldMultiLangService).namedValues(
        column.version -> entity.version,
        column.createdAt -> entity.createdAt,
        column.updatedAt -> entity.updatedAt,
        column.coreId -> entity.coreId.value,
        column.langId -> entity.langId.value,
        column.name -> entity.name
      )
    }.updateAndReturnGeneratedKey.apply()
  }

  //
  //  def opt(dfm: SyntaxProvider[DbFieldMultiLang])(rs: WrappedResultSet): Option[DbFieldMultiLang] = {
  //    rs.longOpt(dfm.resultName.id).map(_ => DbFieldMultiLangService(dfm)(rs))
  //  }

}
