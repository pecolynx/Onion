package services

import com.typesafe.scalalogging.LazyLogging
import models.core.{AppUser, ModelIdImplT, ModelIdT, Page}
import models.exceptions.{AppUserNotFoundException, ModelNotFoundException}
import org.joda.time.DateTime
import org.springframework.security.crypto.password.StandardPasswordEncoder
import scalikejdbc._

/**
  * アプリケーションユーザーサービス。
  */
object AppUserService extends SQLSyntaxSupport[AppUser] with LazyLogging {
  override val tableName = "app_user_list"
  override val autoSession = AutoSession

  val au = AppUserService.syntax("au")

  def apply(au: SyntaxProvider[AppUser])(rs: WrappedResultSet): AppUser = apply(au.resultName)(rs)

  def apply(au: ResultName[AppUser])(rs: WrappedResultSet): AppUser = new AppUser(
    id = new ModelIdImplT[Int](rs.get(au.id)),
    version = rs.get(au.version),
    createdAt = rs.get(au.createdAt),
    updatedAt = rs.get(au.updatedAt),
    loginId = rs.get(au.loginId),
    loginPassword = rs.get(au.loginPassword),
    email = rs.get(au.email),
    name = rs.get(au.name)
  )

  /**
    * キーともとにモデルを取得します。
    *
    * @param id      ログインId
    * @param session セッション
    * @return モデル
    */
  def getModelById(id: ModelIdT[Int])
                  (implicit session: DBSession = autoSession): Option[AppUser] = {
    val option = this.getModelByIdInternal(id)
    if (option.isEmpty) {
      //throw new AppUserNotFoundException("id : " + id)
      logger.warn("AppUser not found. id : " + id)
    }

    return option
  }

  /**
    * キーともとにモデルを取得します。
    *
    * @param loginId ログインId
    * @param session セッション
    * @return モデル
    */
  def getModelByKey(loginId: String)(implicit session: DBSession = autoSession): Option[AppUser] = {
    val option = this.getModelByKeyInternal(loginId)
    if (option.isEmpty) {
      //throw new AppUserNotFoundException("loginId : " + loginId)
      logger.warn("AppUser not found. loginId : " + loginId)
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
  def getModelList(page: Page)(implicit session: DBSession = autoSession): List[AppUser] = {
    withSQL {
      select.from(AppUserService as au).orderBy(au.id).limit(page.size).offset((page.page - 1) * page.size)
    }.map(AppUserService(au.resultName)).list.apply()
  }

  /**
    * アプリケーションユーザーを追加します。
    *
    * @param appUser アプリケーションユーザー
    * @param session セッション
    * @return アプリケーションユーザーId
    */
  def addModel(appUser: AppUser)(implicit session: DBSession = autoSession): ModelIdT[Int] = {
    val passwordEncoder = new StandardPasswordEncoder()
    val generatedKey = withSQL {
      insert.into(AppUserService).namedValues(
        column.version -> appUser.version,
        column.createdAt -> appUser.createdAt,
        column.updatedAt -> appUser.updatedAt,
        column.loginId -> appUser.loginId,
        column.loginPassword -> passwordEncoder.encode(appUser.loginPassword),
        column.email -> appUser.email,
        column.name -> appUser.name
      )
    }.updateAndReturnGeneratedKey.apply()
    new ModelIdImplT[Int](generatedKey.toInt)
  }

  def removeModel(id: ModelIdT[Int], version: Int)(implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      delete.from(AppUserService).where.eq(column.id, id.value).and.eq(column.version, version)
    }.update.apply()

    if (count != 1) {
      throw new AppUserNotFoundException("id : " + id.value)
    }
  }

  /**
    * 全てのアプリケーションユーザーを削除します。
    *
    * @param session セッション
    */
  def removeAll()(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(AppUserService)
    }.update.apply()
  }

  /**
    * アプリケーションユーザーを更新します。
    *
    * @param appUser アプリケーションユーザー
    * @param session セッション
    */
  def updateModel(appUser: AppUser)(implicit session: DBSession = autoSession): Unit = {
    val passwordEncoder = new StandardPasswordEncoder()
    val count = withSQL {
      update(AppUserService).set(
        column.version -> (appUser.version + 1),
        column.updatedAt -> new DateTime(),
        column.loginId -> appUser.loginId,
        column.loginPassword -> passwordEncoder.encode(appUser.loginPassword),
        column.email -> appUser.email,
        column.name -> appUser.name
      ).where.eq(column.id, appUser.id.value).and.eq(column.version, appUser.version)
    }.update.apply()

    if (count != 1) {
      throw new AppUserNotFoundException("id : " + appUser.id.value)
    }
  }

  /**
    * アプリケーションユーザーが存在するか確認します。
    *
    * @param loginId ログインId
    * @param session セッション
    * @return 存在するかどうか
    */

  def containsModel(loginId: String)(implicit session: DBSession = autoSession): Boolean = {
    return this.getModelByKeyInternal(loginId).isDefined
  }

  private def getModelByIdInternal(id: ModelIdT[Int])(implicit session: DBSession = autoSession)
  : Option[AppUser] = {
    withSQL {
      select.from(AppUserService as au).where.append(sqls.eq(au.id, id.value))
    }.map(AppUserService(au.resultName)).single.apply()
  }

  private def getModelByKeyInternal(loginId: String)
                                   (implicit session: DBSession = autoSession): Option[AppUser] = {
    withSQL {
      select.from(AppUserService as au).where.append(sqls.eq(au.loginId, loginId))
    }.map(AppUserService(au.resultName)).single.apply()
  }
}
