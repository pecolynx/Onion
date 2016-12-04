package controllers

import com.fasterxml.jackson.databind.JsonMappingException
import models.AppSettings
import models.exceptions.{BadRequestException, DocumentNotFoundException}
import org.joda.time.DateTime
import play.api.mvc.{AnyContent, Request}
import service.FileInfoService
import viewmodelconverters.VmDocumentConverter
import viewmodels.file._
import viewmodels.VmDocumentFile
import com.kujilabo.validation.ModelValidationException
import com.kujilabo.models.core._
import com.kujilabo.common._
import com.kujilabo.models.elasticsearch.es.{EsDocument, EsDocumentFieldList}
import com.kujilabo.service.elasticsearch.DocumentService
import com.kujilabo.util.JsonUtils
import models.core._

import scala.util.{Failure, Success, Try}

/**
  * ドキュメントヘルパー。
  */
object DocumentFileControllerHelper {

  def getVmFileAddParameter(request: Request[AnyContent]): VmFileAddParameter = {
    this.getParameter[VmFileAddParameter](request, classOf[VmFileAddParameter])
  }

  def getVmFileUpdateParameter(request: Request[AnyContent]): VmFileUpdateParameter = {
    this.getParameter[VmFileUpdateParameter](request, classOf[VmFileUpdateParameter])
  }

  def getVmFileGetParameter(request: Request[AnyContent]): VmFileGetParameter = {
    this.getParameter[VmFileGetParameter](request, classOf[VmFileGetParameter])
  }

  def getVmFileSearchParameter(request: Request[AnyContent]): VmFileSearchParameter = {
    this.getParameter[VmFileSearchParameter](request, classOf[VmFileSearchParameter])
  }

  def getVmFileListParameter(request: Request[AnyContent]): VmFileListParameter = {
    this.getParameter[VmFileListParameter](request, classOf[VmFileListParameter])
  }

  def getParameter[T <: BaseObject](request: Request[AnyContent], valueType: Class[T]): T = {
    try {
      val vm = JsonUtils.toObject(request.body.asJson.get.toString(), valueType)
      vm.validate[T](vm)
      vm
    }
    catch {
      case ex: JsonMappingException =>
        ex.printStackTrace()
        if (ex.getCause == null) {
          throw new BadRequestException(ex.getMessage)
        }
        ex.getCause match {
          case ex: ModelValidationException =>
            ex.printStackTrace()
            throw new BadRequestException(ex.getMessage)
        }
      case ex: NoSuchElementException =>
        ex.printStackTrace()
        throw new BadRequestException("Bad Request")
    }
  }

  /**
    * リクエストからファイルドキュメントビューモデルを取得します。
    *
    * @param request リクエスト
    * @return ファイルドキュメントビューモデル
    */
  def getVmDocumentFile(request: Request[AnyContent]): VmDocumentFile = {
    JsonUtils.toObject(request.body.asJson.get.toString(), classOf[VmDocumentFile])
  }

  def clearFile(appUserId: ModelIdT[Int], clientId: String, appSettings: AppSettings): Unit = {
    // 対象ユーザーのレコードのみ削除
    FileInfoService.removeModelByUser(appUserId, clientId)

    //
    DocumentService.removeDocumentByUser(appSettings.esUrl(), appSettings.fileIndex(),
      appSettings.fileMapping(), appUserId)
  }

  def addFileModel(appUserId: ModelIdT[Int], clientId: String, vmDocumentFile: VmDocumentFile,
                   appSettings: AppSettings): ModelIdT[String] = {
    val model = VmDocumentConverter.toModelForAdd(vmDocumentFile, appSettings.fileIndex(),
      appSettings.fileMapping(), appUserId)

    val x: Option[ModelIdT[String]] = for {
      modelId <- DocumentService.addDocument(appSettings.esUrl(), model)
    } yield {
      val result = this.addFile(appUserId, clientId, vmDocumentFile, modelId)
      modelId
    }

    if (x.isEmpty) throw new CommonException("")
    else x.get
  }

  def addFile(appUserId: ModelIdT[Int], clientId: String,
              vm: VmDocumentFile, documentId: ModelIdT[String]): Try[Unit] = {
    val documentSize = vm.fileContent.length()
    val fileHash = vm.fileHash
    val fileInfo = new FileInfo(appUserId, clientId, vm.filePath,
      documentId, documentSize, fileHash, false, new DateTime(), new DateTime(), null)
    FileInfoService.addModel(fileInfo)
  }

  def removeFileModel(appUserId: ModelIdT[Int], clientId: String, filePath: String,
                      appSettings: AppSettings): Unit = {
    for {
      oldFileInfo <- FileInfoService.getModelByKey(appUserId, clientId, filePath)
    } yield {
      val oldDocument = getDocument(appSettings, oldFileInfo.documentId)
      oldDocument match {
        case None => throw new DocumentNotFoundException("", oldFileInfo.documentId.value)
        case Some(x) =>
          FileInfoService.removeModel(appUserId, clientId, filePath)
          DocumentService.removeDocument(appSettings.esUrl(), appSettings.fileIndex(),
            appSettings.fileMapping(), x.id)
      }
    }
  }

  def updateFileModel(appUserId: ModelIdT[Int], clientId: String, vmDocumentFile: VmDocumentFile,
                      appSettings: AppSettings): ModelIdT[String] = {

    val documentSize = vmDocumentFile.fileContent.length()
    val fileHash = vmDocumentFile.fileHash

    val x = for {
      oldFileInfo <- FileInfoService.getModelByKey(appUserId, clientId, vmDocumentFile.filePath)
    } yield {
      val newFileInfo = this.cloneFileInfo(oldFileInfo, documentSize, fileHash, false)
      val oldDocument = getDocument(appSettings, oldFileInfo.documentId)
      oldDocument match {
        case None => throw new DocumentNotFoundException("", oldFileInfo.documentId.value)
        case Some(x) =>
          val newDocument = cloneFileModel(x, vmDocumentFile.documentFieldList)
          FileInfoService.updateModel(newFileInfo)
          DocumentService.updateDocument(appSettings.esUrl(), newDocument)

          return newDocument.id
      }
    }

    x.get
  }

  def getDocument(appSettings: AppSettings, documentId: ModelIdT[String]): Option[EsDocument] = {
    DocumentService.getDocument(appSettings.esUrl(), appSettings.fileIndex(),
      appSettings.fileMapping(), documentId)
  }

  def cloneFileModel(model: EsDocument, documentFieldList: EsDocumentFieldList): EsDocument = {
    new EsDocument(model.id, model.version, model.createdAt, model.updatedAt,
      model.indexName, model.mappingName, model.title, model.createdBy,
      documentFieldList)
  }

  def cloneFileInfo(model: FileInfo, documentSize: Int, documentHash: String, removed: Boolean):
  FileInfo = {
    val removedAt = {
      if (removed) model.removedAt
      else null
    }

    new FileInfo(model.appUserId, model.clientId, model.filePath, model.documentId,
      documentSize, documentHash, removed, model.createdAt, model.updatedAt, removedAt)
  }
}
