package controllers

import com.google.inject.{Guice, Injector}
import com.kujilabo.models.core.{Page, VariableName}
import com.kujilabo.models.elasticsearch._
import com.kujilabo.models.elasticsearch.db.DbFieldType.DbFieldTypeText
import com.kujilabo.models.elasticsearch.es.EsFieldAnalyzer.{EsFieldAnalyzerKuromoji, EsFieldAnalyzerNone}
import com.kujilabo.models.elasticsearch.es.EsFieldFormat.{EsFieldFormatDateOptionalTime, EsFieldFormatNone}
import com.kujilabo.models.elasticsearch.es.EsFieldType.{EsFieldTypeDate, EsFieldTypeInteger, EsFieldTypeString}
import com.kujilabo.models.elasticsearch.es.custom.EsMappingFile
import com.kujilabo.service.elasticsearch.db.DbIndexService
import com.kujilabo.service.elasticsearch.es.EsIndexService
import com.kujilabo.service.elasticsearch.{IndexService, MappingService}
import config.TestServiceModule
import models.AppSettingsTestImpl
import models.core.AppUser
import org.joda.time.DateTime
import org.scalatest._
import scalikejdbc.ConnectionPool
import service.FileInfoService
import services.AppUserService
import viewmodels.VmDocumentFile

class DocumentControllerHelperTest extends FunSpec with BeforeAndAfter with
  ShouldMatchers {
  val URL = "http://onion.kujilabo.com:9200"
  val ADDRESS = "onion.kujilabo.com"
  val PORT = 9300
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_file_index"))
  val TEST_MAPPING_NAME = new MappingName(new VariableName("test_file_mapping"))
  val FIELD_FILE_PATH = new VariableName("file_path")
  val FIELD_FILE_NAME = new VariableName("file_name")
  val FIELD_CONTENT = new VariableName("content")
  val TEST_USER_ID = 456
  var index: Index = null

  var injector: Injector = _
  println("***before***")
  val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
  ConnectionPool.singleton(settings.url, settings.user, settings.password)

  def initialize(): Unit = {
    if (DbIndexService.containsModel(TEST_INDEX_NAME)) {
      val dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
      DbIndexService.removeModel(dbIndex.id, dbIndex.version)
    }

    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    IndexService.addIndex(URL, new Index(TEST_INDEX_NAME))
    index = IndexService.getIndex(URL, TEST_INDEX_NAME)
    MappingService.addMapping(URL, TEST_INDEX_NAME, new Mapping(index, TEST_MAPPING_NAME))

    val mapping = MappingService.getMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME)
    val userFieldList = new FieldList(List(
      new Field(
        FIELD_FILE_PATH, "ファイルパス", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 1),
      new Field(
        FIELD_FILE_NAME, "ファイル名", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 2),
      new Field(
        FIELD_CONTENT, "内容", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 3),
      new Field(
        EsMappingFile.FILE_EXT, "拡張子", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 4),
      new Field(
        EsMappingFile.FILE_SIZE, "ファイルサイズ", true, true, true,
        EsFieldTypeInteger, EsFieldFormatNone, EsFieldAnalyzerNone,
        DbFieldTypeText, 5),
      new Field(
        EsMappingFile.FILE_CREATED_AT, "ファイル作成日時", true, true, true,
        EsFieldTypeDate, EsFieldFormatDateOptionalTime, EsFieldAnalyzerNone,
        DbFieldTypeText, 6),
      new Field(
        EsMappingFile.FILE_UPDATED_AT, "ファイル更新日時", true, true, true,
        EsFieldTypeDate, EsFieldFormatDateOptionalTime, EsFieldAnalyzerNone,
        DbFieldTypeText, 7)
    ))
    MappingService.updateMapping(URL, index, mapping, userFieldList)
  }

}

class DocumentControllerHelperTest_addFileModel extends DocumentControllerHelperTest {
  describe("a") {
    it("b") {
      AppUserService.removeAll
      val appSettings = new AppSettingsTestImpl
      val appUserId = AppUserService.addModel(new AppUser("loginId", "password", "email", "name"))
      val clientId = ""
      val vmDocumentFile = new VmDocumentFile("abc.txt", "C:\\abc.txt", "xyz", "123", 100,
        "2000/01/01 10:11:12", "2000/01/01 10:11:12")

      val modelId = DocumentFileControllerHelper.addFileModel(appUserId, clientId, vmDocumentFile,
        appSettings)

      val modelList = FileInfoService.getModelList(appUserId, clientId, new Page(1, 10))
      modelList.size should equal(1)

      val fileInfo = FileInfoService.getModelByKey(appUserId, clientId, "C:\\abc.txt").get
      fileInfo.filePath should equal("C:\\abc.txt")

      val contains = FileInfoService.containsModel(appUserId, clientId, "C:\\abc.txt")
    }
  }
}


class DocumentControllerHelperTest_updateFileModel extends DocumentControllerHelperTest {
  describe("a") {
    it("b") {
      this.initialize()

      AppUserService.removeAll
      val appSettings = new AppSettingsTestImpl
      val appUserId = AppUserService.addModel(new AppUser("loginId", "password", "email", "name"))
      val clientId = ""
      val vmDocumentFile = new VmDocumentFile("abc.txt", "C:\\abc.txt", "xyz", "123", 100,
        "2000/01/01 10:11:12", "2000/01/01 10:11:12")

      val modelId = DocumentFileControllerHelper.addFileModel(appUserId, clientId, vmDocumentFile,
        appSettings)

      DocumentFileControllerHelper.updateFileModel(appUserId, clientId, vmDocumentFile, appSettings)

      val contains = FileInfoService.containsModel(appUserId, clientId, "C:\\abc.txt")
    }
  }
}

class DocumentControllerHelperTest_removeFileModel extends DocumentControllerHelperTest {
  describe("a") {
    it("b") {
      AppUserService.removeAll
      val appSettings = new AppSettingsTestImpl
      val appUserId = AppUserService.addModel(new AppUser("loginId", "password", "email", "name"))
      val clientId = ""
      val vmDocumentFile = new VmDocumentFile("abc.txt", "C:\\abc.txt", "xyz", "123", 100,
        "2000/01/01 10:11:12", "2000/01/01 10:11:12")

      val modelId = DocumentFileControllerHelper.addFileModel(appUserId, clientId, vmDocumentFile,
        appSettings)

      DocumentFileControllerHelper.removeFileModel(appUserId, clientId, "C:\\abc.txt", appSettings)

      val contains = FileInfoService.containsModel(appUserId, clientId, "C:\\abc.txt")
    }
  }
}
