package controllers

import com.google.inject.{Guice, Injector}
import com.kujilabo.models.core.VariableName
import config.{ServiceModule, TestServiceModule}
import models.AppSettingsTestImpl
import models.core.Title
import models.elasticsearch.db.DbFieldType.DbFieldTypeText
import models.elasticsearch.es.{EsDocument, EsDocumentFieldList, EsDocumentFieldString}
import models.elasticsearch.es.EsFieldAnalyzer.EsFieldAnalyzerKuromoji
import models.elasticsearch.es.EsFieldFormat.EsFieldFormatNone
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import models.elasticsearch.{MappingName, _}
import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import scalikejdbc.{ConnectionPool, NamedDB}
import service.elasticsearch.{IndexService, MappingService}
import service.elasticsearch.db.DbIndexService
import service.elasticsearch.es.{EsDocumentService, EsIndexService}
import viewmodels.file.VmFileSearchParameter

class DocumentControllerTest extends FunSpec with BeforeAndAfter with
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

  println("***before***")
  val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
  ConnectionPool.singleton(settings.url, settings.user, settings.password)
}


class DocumentControllerTest_searchByKeyword extends DocumentControllerTest {
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
        DbFieldTypeText, 3)
    ))
    MappingService.updateMapping(URL, index, mapping, userFieldList)
  }

  var injector: Injector = _
  //  describe("a") {
  //    it("b") {
  //
  //      injector = Guice.createInjector(new TestServiceModule)
  //      //val target1 = new DocumentController(new AppSettingsTestImpl)
  //      val target = injector.getInstance(classOf[DocumentController])
  //
  //      this.initialize()
  //
  //      val esDocumentFieldList = new EsDocumentFieldList(List(
  //        new EsDocumentFieldString(FIELD_FILE_PATH, "C:\\新規ドキュメント.txt"),
  //        new EsDocumentFieldString(FIELD_FILE_NAME, "新規ドキュメント.txt"),
  //        new EsDocumentFieldString(FIELD_CONTENT, "東京都渋谷区恵比寿1-10-17")
  //      ))
  //      val document = new EsDocument(TEST_INDEX_NAME, TEST_MAPPING_NAME,
  //        new Title("新規ドキュメント.txt"), TEST_USER_ID, esDocumentFieldList)
  //      EsDocumentService.addDocument(URL, document)
  //
  //      Thread.sleep(1000)
  //
  //      val vmSearchResult = target.searchByKeyword(
  //        new VmFileSearchParameter("loginId", "authToken", "新規", 1, 10))
  //      vmSearchResult.totalCount should equal(1)
  //    }
  //  }
}