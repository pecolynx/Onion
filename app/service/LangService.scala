package service

import models.core.{Lang, ModelIdImplT, Page}
import scalikejdbc.{WrappedResultSet, _}

object LangService extends SQLSyntaxSupport[Lang] {
  override val tableName = "lang_list"
  override val autoSession = AutoSession

  override val columns = Seq(
    "ID",
    "CODE", "NAME"
  )

  val l = LangService.syntax("l")

  def apply(l: SyntaxProvider[Lang])(rs: WrappedResultSet): Lang = apply(l.resultName)(rs)

  def apply(l: ResultName[Lang])(rs: WrappedResultSet): Lang = {
    new Lang(
      id = new ModelIdImplT[Int](rs.get(l.id)),
      code = rs.get(l.code),
      name = rs.get(l.name)
    )
  }

  def getModelByKey(code: String)(implicit session: DBSession = autoSession): Option[Lang] = {
    withSQL {
      select.from(LangService as l).where.append(
        sqls.eq(l.code, code))
    }.map(LangService(l.resultName)).single.apply()
  }

  def getModelList(page: Page)(implicit session: DBSession = autoSession): List[Lang] = {
    withSQL {
      select.from(LangService as l).orderBy(l.id).limit(page.size).offset((page.page - 1) * page.size)
    }.map(LangService(l.resultName)).list.apply()
  }

  //  def addModel(entity: Lang)(implicit session: DBSession = autoSession): Unit = {
  //    val generatedKey = withSQL {
  //      insert.into(LangService).namedValues(
  //        column.code -> entity.code,
  //        column.name -> entity.name
  //      )
  //    }.updateAndReturnGeneratedKey.apply()
  //  }
  //
  //  def removeAll()(implicit session: DBSession = autoSession): Unit = {
  //    withSQL {
  //      delete.from(LangService)
  //    }.update.apply()
  //  }
}
