package service

import com.google.inject.Inject
import com.typesafe.scalalogging.LazyLogging
import models.AppSettings
import models.core.{AppUser, AppUserAuthToken, ModelIdImplT, ModelIdT}
import models.exceptions.ModelNotFoundException
import org.joda.time.DateTime
import scalikejdbc._
import services.AppUserService._

import scala.util.Try

/**
  * 認証サービス。
  */
object AppUserAuthTokenService extends SQLSyntaxSupport[AppUserAuthToken] with LazyLogging {
  override val tableName = "app_user_auth_token_list"
  override val autoSession = AutoSession

  val auat = AppUserAuthTokenService.syntax("auat")

  def apply(auat: SyntaxProvider[AppUserAuthToken])(rs: WrappedResultSet): AppUserAuthToken = apply(auat.resultName)(rs)

  def apply(auat: ResultName[AppUserAuthToken])(rs: WrappedResultSet): AppUserAuthToken = {
    new AppUserAuthToken(
      appUserId = new ModelIdImplT[Int](rs.get(auat.appUserId)),
      authToken = rs.get(auat.authToken),
      expires = rs.get(auat.expires)
    )
  }

  def getModelByKey(appUserId: ModelIdT[Int])
                   (implicit session: DBSession = autoSession): Option[AppUserAuthToken] = {
    val option = this.getModelByKeyInternal(appUserId)
    if (option.isEmpty) {
      //throw new ModelNotFoundException("{ appUserId : " + appUserId + " }")
      logger.warn("{ appUserId : " + appUserId + " }")
    }

    option
  }

  def getModelByAuthToken(authToken: String)
                         (implicit session: DBSession = autoSession): Option[AppUserAuthToken] = {
    val option = this.getModelByAuthTokenInternal(authToken)
    if (option.isEmpty) {
      //throw new ModelNotFoundException("{ authToken : " + authToken + " }")
      logger.warn("AuthToken not found. authToken : " + authToken);
    }

    option
  }

  /**
    * モデルを追加します。
    *
    * @param appUserAuthToken アプリケーションユーザー認証情報。
    * @param authTokenExpiresDays
    * @param session          セッション
    */
  def addModel(appUserAuthToken: AppUserAuthToken, authTokenExpiresDays: Int)
              (implicit session: DBSession = autoSession): Try[Unit] = Try {
    withSQL {
      insert.into(AppUserAuthTokenService).namedValues(
        column.appUserId -> appUserAuthToken.appUserId.value,
        column.authToken -> appUserAuthToken.authToken,
        column.expires -> new DateTime().plusDays(authTokenExpiresDays)
      )
    }.update.apply()
  }

  /**
    * モデルを削除します。
    *
    * @param appUserId アプリケーションユーザーId
    * @param session   セッション
    */
  def removeModel(appUserId: ModelIdT[Int])(implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      delete.from(AppUserAuthTokenService).where.eq(column.appUserId, appUserId.value)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("appUserId : " + appUserId.value)
    }
  }

  def updateToken(appUserAuthToken: AppUserAuthToken, authTokenExpiresDays: Int)
                 (implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      update(AppUserAuthTokenService).set(
        column.appUserId -> appUserAuthToken.appUserId.value,
        column.authToken -> appUserAuthToken.authToken,
        column.expires -> new DateTime().plusDays(authTokenExpiresDays)
      ).where.eq(column.appUserId, appUserAuthToken.appUserId.value)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + appUserAuthToken.appUserId.value)
    }
  }

  def updateExpires(appUserId: ModelIdT[Int], authTokenExpiresDays: Int)
                   (implicit session: DBSession = autoSession): Unit = {
    val count = withSQL {
      update(AppUserAuthTokenService).set(
        column.expires -> new DateTime().plusDays(authTokenExpiresDays)
      ).where.eq(column.appUserId, appUserId.value)
    }.update.apply()

    if (count != 1) {
      throw new ModelNotFoundException("id : " + appUserId.value)
    }
  }

  def containsModel(appUserId: ModelIdT[Int])(implicit session: DBSession = autoSession)
  : Boolean = {
    return this.getModelByKeyInternal(appUserId).isDefined
  }

  private def getModelByKeyInternal(appUserId: ModelIdT[Int])
                                   (implicit session: DBSession = autoSession)
  : Option[AppUserAuthToken] = {
    withSQL {
      select.from(AppUserAuthTokenService as auat).
        where.append(sqls.eq(auat.appUserId, appUserId.value))
    }.map(AppUserAuthTokenService(auat.resultName)).single.apply()
  }

  private def getModelByAuthTokenInternal(authToken: String)
                                         (implicit session: DBSession = autoSession)
  : Option[AppUserAuthToken] = {
    withSQL {
      select.from(AppUserAuthTokenService as auat).
        where.append(sqls.eq(auat.authToken, authToken))
    }.map(AppUserAuthTokenService(auat.resultName)).single.apply()
  }
}
