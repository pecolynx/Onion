package controllers

import com.typesafe.scalalogging.LazyLogging
import models.elasticsearch.es.EsDocument
import models.exceptions._
import play.api.mvc.{AnyContent, Controller, Request, Result}
import service.AppUserAuthTokenService
import services.AppUserService
import utils.JsonUtils
import models.core._
import com.kujilabo.models.core._
import models.core._

/**
  * 基底コントローラ。
  */
class ControllerBase extends Controller with LazyLogging {

  val MSG_INTERNAL_SERVER_ERROR: String = JsonUtils.toJson(Map("result" -> "Internal Server Error"))
  val MSG_UNAUTHORIZED: String = JsonUtils.toJson(Map("result" -> "Unauthorized"))
  val MSG_OK: String = JsonUtils.toJson(Map("result" -> "OK"))
  val MSG_FORBIDDEN: String = JsonUtils.toJson(Map("result" -> "Forbidden"))
  val MSG_BAD_REQUEST: String = JsonUtils.toJson(Map("result" -> "Bad Request"))

  def getUser(appUserId: ModelIdT[Int]): Option[AppUser] = {
    AppUserService.getModelById(appUserId)
  }

  def getToken(authToken: String): Option[AppUserAuthToken] = {
    AppUserAuthTokenService.getModelByAuthToken(authToken)
  }

  object withAuth {
    def apply(authToken: String,
              getToken: String => Option[AppUserAuthToken],
              getUser: ModelIdT[Int] => Option[AppUser],
              function: AppUser => Result): Result = {
      val appUserAuthToken = getToken(authToken)
      val x = for {
        token <- appUserAuthToken
        user <- getUser(token.appUserId)
      } yield function(user)

      x.getOrElse(Unauthorized(MSG_UNAUTHORIZED))
    }
  }

  object withOwner {
    def apply(model: EsDocument, appUser: AppUser, function: EsDocument => Result): Result = {
      if (model.createdBy.toString != appUser.id.value.toString) {
        logger.warn("createdBy = " + model.createdBy.toString)
        logger.warn("appUser.id.value = " + appUser.id.value.toString)
        throw new ModelNotFoundException("Document id : " + model.id.value)
      }

      function(model)
    }
  }

  object action {
    def apply(function: () => Result): Result = {
      try {
        val result: Result = function()
        return result
      }
      catch {
        case ex: BadRequestException =>
          logger.warn("message = " + ex.getMessage)
          ex.printStackTrace()
          BadRequest(JsonUtils.toJson(Map("message" -> ex.getMessage)))
        case ex: AppUserNotFoundException =>
          logger.warn("message = " + ex.getMessage)
          ex.printStackTrace()
          Unauthorized(MSG_UNAUTHORIZED)
        case ex: DocumentNotFoundException =>
          logger.warn("message = " + ex.getMessage)
          ex.printStackTrace()
          NotFound(JsonUtils.toJson(Map("result" -> ("Document not found. id : " + ex.documentId))))
        case ex: NotAuthorizedException =>
          logger.warn("message = " + ex.getMessage)
          ex.printStackTrace()
          Unauthorized(MSG_UNAUTHORIZED)
        case ex: Exception =>
          logger.warn("message = " + ex.getMessage)
          ex.printStackTrace()
          InternalServerError(MSG_INTERNAL_SERVER_ERROR)
      }
    }
  }

  def getAuthToken(request: Request[AnyContent]): String = {
    val authTokenList = request.queryString.getOrElse("authToken", Seq[String]())
    if (authTokenList.isEmpty) {
      throw new BadRequestException(MSG_BAD_REQUEST)
    }

    return authTokenList(0)
  }
}
