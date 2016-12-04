package controllers

import javax.inject.Inject

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.scalalogging.LazyLogging
import models.core._
import models.exceptions.{AppUserNotFoundException, BadRequestException, DocumentNotFoundException}
import models.{AppSettings, DBSettings}
import org.springframework.web.client.HttpClientErrorException
import play.api.mvc._
import service.{AppUserAuthTokenService, FileInfoService, LangService}
import services.AppUserService
import viewmodelconverters.{VmDocumentConverter, VmFileSearchConditionConverter, VmSearchResultConverter}
import viewmodels.file.VmFileSearchParameter
import viewmodels.{VmCheckAuthParameter, VmDocumentFile, VmSearchResult}
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.{FileSearchCondition, SearchResult}
import com.kujilabo.models.elasticsearch.es.EsDocument
import com.kujilabo.service.elasticsearch.{DocumentFileSearchService, DocumentService}
import com.kujilabo.util.JsonUtils

/**
  * ドキュメントコントローラ。
  *
  * @param appSettings
  */
class DocumentFileController @Inject()(appSettings: AppSettings) extends ControllerBase {


  def clearFile(appUserId: ModelIdT[Int], clientId: String): Unit = {
    DocumentFileControllerHelper.clearFile(appUserId, clientId, this.appSettings)
  }

  /**
    * ファイルドキュメントを初期化します。
    *
    * @return 初期化結果
    */
  def file_clear = Action { request =>
    action { () =>
      val authToken = this.getAuthToken(request)
      withAuth(authToken, getToken, getUser, { appUser: AppUser =>
        val clientId = ""
        this.clearFile(appUser.id, clientId)
        Ok(MSG_OK)
      })
    }
  }

  def updateFile(appUserId: ModelIdT[Int], clientId: String, vm: VmDocumentFile): ModelIdT[String] = {
    DocumentFileControllerHelper.updateFileModel(appUserId, clientId, vm, this.appSettings)
  }

  def addFile(appUserId: ModelIdT[Int], clientId: String, vm: VmDocumentFile): ModelIdT[String] = {
    DocumentFileControllerHelper.addFileModel(appUserId, clientId, vm, this.appSettings)
  }

  def executeAddOrUpdate(containsDb: Boolean, appUserId: ModelIdT[Int], clientId: String,
                         vm: VmDocumentFile): ModelIdT[String] = {
    if (containsDb) {
      this.updateFile(appUserId, clientId, vm)
    }
    else {
      this.addFile(appUserId, clientId, vm)
    }
  }

  def containsFile(appUserId: ModelIdT[Int], clientId: String, filePath: String): Boolean = {
    FileInfoService.containsModel(appUserId, clientId, filePath)
  }

  /**
    * ファイルドキュメントを追加します。
    *
    * @return モデルId
    */
  def file_add = Action { request =>
    logger.warn("file_add")

    action { () =>
      val authToken = this.getAuthToken(request)
      val vm = DocumentFileControllerHelper.getVmFileAddParameter(request)

      logger.info("file path : " + vm.documentFile.filePath)
      withAuth(authToken, getToken, getUser, { appUser: AppUser =>
        val clientId = ""
        val containsDb = this.containsFile(appUser.id, clientId, vm.documentFile.filePath)
        val modelId = this.executeAddOrUpdate(containsDb, appUser.id, clientId, vm.documentFile)
        Ok(JsonUtils.toJson(Map("documentId" -> modelId.value)))
      })
    }
  }

  /**
    * ファイルドキュメントを削除します。
    *
    * @param id ドキュメントId
    * @return 削除結果
    */
  def file_remove(id: String) = Action { request =>
    logger.warn("file_remove")
    action { () =>
      val authToken = this.getAuthToken(request)
      withAuth(authToken, getToken, getUser, { appUser: AppUser =>
        val model = this.getDocument(new ModelIdImplT[String](id))
        val x = for {
          m <- model
        } yield {
          withOwner(m, appUser, { model: EsDocument =>
            this.removeDocument(model.id)
            Ok(MSG_OK)
          })
        }
        x.getOrElse(NotFound(""))
      })
    }
  }

  /**
    * ファイルドキュメントを更新します。
    *
    * @param id ドキュメントId
    * @return 更新ドキュメントId
    */
  def file_update(id: String) = Action { request =>
    logger.warn("file_update")
    action { () =>
      val vm = DocumentFileControllerHelper.getVmFileUpdateParameter(request)
      logger.info("file path : " + vm.documentFile.filePath)
      val authToken = this.getAuthToken(request)
      withAuth(authToken, this.getToken, this.getUser, { appUser =>
        val model = this.getDocument(new ModelIdImplT[String](id))
        val x = for {
          m <- model
        } yield {
          withOwner(m, appUser, { model: EsDocument =>
            val clientId = ""
            val modelId = this.updateDocument(appUser.id, clientId, vm.documentFile)
            Ok(JsonUtils.toJson(Map("documentId" -> modelId.value)))
          })
        }

        x.getOrElse(NotFound(""))
      })
    }
  }

  /**
    * ファイルドキュメントを取得します。
    *
    * @param id ドキュメントId
    * @return ファイルドキュメント
    */
  def file_get(id: String) = Action { request =>
    action { () =>
      val vm = DocumentFileControllerHelper.getVmFileGetParameter(request)
      val authToken = this.getAuthToken(request)
      withAuth(authToken, this.getToken, this.getUser, { appUser =>
        val model = this.getDocument(new ModelIdImplT[String](vm.documentFile.id))
        val x = for {
          m <- model
        } yield {
          withOwner(m, appUser, { model =>
            Ok(JsonUtils.toJson(VmDocumentConverter.toViewModel(model)))
          })
        }

        x.getOrElse(NotFound(""))
      })
    }
  }

  /**
    * ファイルドキュメントを検索します。
    *
    * @return 検索結果
    */
  def file_search = Action { request =>
    action { () =>
      logger.warn("search json = " + request.body.asJson)
      val vm = DocumentFileControllerHelper.getVmFileSearchParameter(request)
      val authToken = this.getAuthToken(request)
      withAuth(authToken, this.getToken, this.getUser, { appUser =>
        val searchCondition = VmFileSearchConditionConverter.toModel(vm.searchCondition)
        val searchResult = this.search(appUser.id, searchCondition)
        val vmSearchResult = VmSearchResultConverter.toViewModel(searchResult)
        Ok(JsonUtils.toJson(vmSearchResult))
      })
    }
  }


  def getDocument(documentId: ModelIdT[String]): Option[EsDocument] = {
    DocumentService.getDocument(this.appSettings.esUrl, this.appSettings.fileIndex(),
      this.appSettings.fileMapping(), documentId)
  }

  def removeDocument(documentId: ModelIdT[String]): Unit = {
    DocumentService.removeDocument(this.appSettings.esUrl(), this.appSettings.fileIndex(),
      this.appSettings.fileMapping(), documentId)
  }

  def updateDocument(appUserId: ModelIdT[Int], clientId: String,
                     vm: VmDocumentFile): ModelIdT[String] = {
    DocumentFileControllerHelper.updateFileModel(appUserId, clientId, vm, this.appSettings)
  }

  def search(appUserId: ModelIdT[Int], searchCondition: FileSearchCondition): SearchResult = {
    DocumentFileSearchService.search(this.appSettings.esUrl,
      this.appSettings.fileIndex(), this.appSettings.fileMapping(), appUserId.value,
      searchCondition)
  }

  def getFilePathList(appUserId: ModelIdT[Int], page: Page): List[String] = {
    DocumentFileSearchService.getFilePathList(
      this.appSettings.esAddress(), this.appSettings.esPort, this.appSettings.esUrl,
      this.appSettings.fileIndex(), this.appSettings.fileMapping(), appUserId.value,
      page)
  }

  /**
    * ファイルパス一覧を取得します。
    *
    * @return ファイルパス一覧
    */
  def file_list = Action { request =>
    action { () =>
      val vm = DocumentFileControllerHelper.getVmFileListParameter(request)
      val page = new Page(vm.page.pageNumber, vm.page.pageSize)
      val authToken = this.getAuthToken(request)
      withAuth(authToken, this.getToken, this.getUser, { appUser =>
        val result = this.getFilePathList(appUser.id, page)
        Ok(JsonUtils.toJson(result))
      })
    }
  }
}
