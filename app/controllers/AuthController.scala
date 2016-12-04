package controllers

import javax.inject.Inject

import com.kujilabo.util.{JsonUtils, RandomUtils}
import com.typesafe.scalalogging.LazyLogging
import models.{AppSettings, DBSettings}
import models.core.{AppUser, AppUserAuthToken}
import org.joda.time.DateTime
import org.springframework.security.crypto.password.{PasswordEncoder, StandardPasswordEncoder}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import service.{AppUserAuthTokenService, LangService}
import services.AppUserService
import viewmodels.{VmAppUserRegisterParameter, VmAuthParameter, VmDocumentFile}

/**
  * 認証コントローラー。
  */
class AuthController @Inject()(appSettings: AppSettings) extends Controller with LazyLogging {
  /**
    * ユーザーを登録します。
    *
    * @return 登録結果(201：成功, 409：失敗)
    */
  def register = Action { request =>
    val vm = this.getVmAppUserRegisterParameter(request)
    logger.warn(vm.loginPassword)
    if (AppUserService.containsModel(vm.loginId)) {
      Conflict("User already exists.")
    }
    else {
      val appUser = new AppUser(vm.loginId, vm.loginPassword, vm.email, vm.name)
      AppUserService.addModel(appUser)
      Created("User created.")
    }
  }

  /**
    * 認証します。
    *
    * @return 認証結果
    */
  def authenticate = Action { request =>
    val vm = this.getVmAuthParameter(request)
    val appUser = AppUserService.getModelByKey(vm.loginId)
    appUser.fold(Unauthorized("Unauthorized."))(x => {
      val passwordEncoder = new StandardPasswordEncoder()
      if (passwordEncoder.matches(vm.loginPassword, x.loginPassword)) {
        val token = new AppUserAuthToken(x.id, RandomUtils.createString(10), new DateTime())

        if (AppUserAuthTokenService.containsModel(x.id)) {
          AppUserAuthTokenService.updateToken(token, this.appSettings.authTokenExpiresDays)
        }
        else {
          AppUserAuthTokenService.addModel(token, this.appSettings.authTokenExpiresDays)
        }

        val result = JsonUtils.toJson(Map("auth_token" -> token.authToken))
        Ok(result)
      }
      else {
        logger.warn(x.loginPassword)
        logger.warn(passwordEncoder.encode(vm.loginPassword))
        logger.warn(vm.loginPassword)
        Unauthorized("Unauthorized.")
      }
    })
  }

  /**
    * リクエストからビューモデルを取得します。
    *
    * @param request リクエスト
    * @return ビューモデル
    */
  private def getVmAppUserRegisterParameter(request: Request[AnyContent]): VmAppUserRegisterParameter = {
    JsonUtils.toObject(request.body.asJson.get.toString(), classOf[VmAppUserRegisterParameter])
  }

  /**
    * リクエストからビューモデルを取得します。
    *
    * @param request リクエスト
    * @return ビューモデル
    */
  private def getVmAuthParameter(request: Request[AnyContent]): VmAuthParameter = {
    JsonUtils.toObject(request.body.asJson.get.toString(), classOf[VmAuthParameter])
  }

}
