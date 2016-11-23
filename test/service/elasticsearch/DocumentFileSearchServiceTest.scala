package service.elasticsearch

import models.core.{Page, Title, VariableName}
import models.elasticsearch.db.DbFieldType.DbFieldTypeText
import models.elasticsearch.es.{EsDocument, EsDocumentFieldList, EsDocumentFieldString}
import models.elasticsearch.es.EsFieldAnalyzer.{EsFieldAnalyzerKuromoji, EsFieldAnalyzerNone}
import models.elasticsearch.es.EsFieldFormat.EsFieldFormatNone
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import models.elasticsearch.{Field, FileSearchCondition, _}
import org.scalatest._
import scalikejdbc.ConnectionPool
import service.elasticsearch.db.DbIndexService
import service.elasticsearch.es.{EsDocumentService, EsIndexService}
import utils.JsonUtils

class DocumentFileSearchServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  val URL = "http://onion.kujilabo.com:9200"
  val ADDRESS = "onion.kujilabo.com"
  val PORT = 9300
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
  val TEST_MAPPING_NAME = new MappingName(new VariableName("test_mapping"))
  val FIELD_FILE_PATH = new VariableName("file_path")
  val FIELD_FILE_NAME = new VariableName("file_name")
  val FIELD_CONTENT = new VariableName("file_content")
  val FIELD_EXT = new VariableName("file_ext")
  val TEST_USER_ID = 456
  val TEST_USER_ID_2 = 789
  var index: Index = null
  before {
    val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)
  }

  describe("a") {
    it("b") {
      val map = Map(
        "hits" -> Map[String, Any](
          "total" -> 6,
          "max_score" -> 0.55,
          "hits" -> List(
            Map(
              "_index" -> "file_index",
              "fields" -> Map[String, Any](
                "file_path" -> List("abc.txt"),
                "created_by" -> List(2)
              ),
              "highlight" -> Map(
                "file_content" -> List("ccc"),
                "title" -> List("aaa")
              )
            )
          )
        ))

      val json = JsonUtils.toJson(map)
      val searchResponse = JsonUtils.toObject(json, classOf[EsSearchResponse])
      println("fields = " + searchResponse.getHits.getHits)
      println("fields = " + searchResponse.getHits.getHits.apply(0).getFields)
      val searchHitFields: Map[String, EsSearchHitField] = searchResponse.getHits.getHits.apply(0).getFields
      val searchHitField: EsSearchHitField = searchHitFields.get("created_by").get
      searchHitField.getValue[Int] should equal(2)
    }
  }

}

class DocumentFileSearchServiceTest_searchByKeyword extends DocumentFileSearchServiceTest {
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
        FIELD_EXT, "拡張子", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerNone,
        DbFieldTypeText, 4)
    ))
    MappingService.updateMapping(URL, index, mapping, userFieldList)
  }

  def createDocument_001(): EsDocument = {
    val esDocumentFieldList = new EsDocumentFieldList(List(
      new EsDocumentFieldString(FIELD_FILE_PATH, "C:\\新規ドキュメント.txt"),
      new EsDocumentFieldString(FIELD_FILE_NAME, "新規ドキュメント.txt"),
      new EsDocumentFieldString(FIELD_CONTENT, "東京都渋谷区恵比寿1-10-17"),
      new EsDocumentFieldString(FIELD_EXT, "txt")
    ))

    new EsDocument(TEST_INDEX_NAME, TEST_MAPPING_NAME,
      new Title("新規ドキュメント.txt"), TEST_USER_ID, esDocumentFieldList)
  }

  def createDocument_002(): EsDocument = {
    val esDocumentFieldList = new EsDocumentFieldList(List(
      new EsDocumentFieldString(FIELD_FILE_PATH, "C:\\abc.txt"),
      new EsDocumentFieldString(FIELD_FILE_NAME, "abc.txt"),
      new EsDocumentFieldString(FIELD_CONTENT, "東京都豊島区サンシャイン"),
      new EsDocumentFieldString(FIELD_EXT, "txt")
    ))

    new EsDocument(TEST_INDEX_NAME, TEST_MAPPING_NAME,
      new Title("abc.txt"), TEST_USER_ID, esDocumentFieldList)
  }

  def createDocument_003(): EsDocument = {
    val esDocumentFieldList = new EsDocumentFieldList(List(
      new EsDocumentFieldString(FIELD_FILE_PATH, "C:\\abc.csv"),
      new EsDocumentFieldString(FIELD_FILE_NAME, "abc.csv"),
      new EsDocumentFieldString(FIELD_CONTENT, "埼玉県"),
      new EsDocumentFieldString(FIELD_EXT, "csv")
    ))

    new EsDocument(TEST_INDEX_NAME, TEST_MAPPING_NAME,
      new Title("abc.csv"), TEST_USER_ID, esDocumentFieldList)
  }

  def createDocument_004(): EsDocument = {
    val esDocumentFieldList = new EsDocumentFieldList(List(
      new EsDocumentFieldString(FIELD_FILE_PATH, "C:\\abc.csv"),
      new EsDocumentFieldString(FIELD_FILE_NAME, "abc.csv"),
      new EsDocumentFieldString(FIELD_CONTENT, "パスワード有効期限確認処理 検索ログファイルローテーション処理"),
      new EsDocumentFieldString(FIELD_EXT, "csv")
    ))

    new EsDocument(TEST_INDEX_NAME, TEST_MAPPING_NAME,
      new Title("abc.csv"), TEST_USER_ID, esDocumentFieldList)
  }

  def createDocument_005(): EsDocument = {
    val esDocumentFieldList = new EsDocumentFieldList(List(
      new EsDocumentFieldString(FIELD_FILE_PATH, "C:\\abc.csv"),
      new EsDocumentFieldString(FIELD_FILE_NAME, "abc.csv"),
      new EsDocumentFieldString(FIELD_CONTENT, "パスワード有効期限確認処理 検索ログファイルローテーション処理"),
      new EsDocumentFieldString(FIELD_EXT, "csv")
    ))

    new EsDocument(TEST_INDEX_NAME, TEST_MAPPING_NAME,
      new Title("abc.csv"), TEST_USER_ID_2, esDocumentFieldList)
  }

  def getValue(documentFieldList: EsDocumentFieldList, key: VariableName): String = {
    documentFieldList.fieldNameValueMap.get(key.value).get.toString
  }


  describe("a") {
    it("強調されること") {
      this.initialize()

      EsDocumentService.addDocument(URL, this.createDocument_001)
      EsDocumentService.addDocument(URL, this.createDocument_002)

      Thread.sleep(1000)

      val searchResult = DocumentFileSearchService.search(
        URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID,
        new FileSearchCondition(new Page(1, 10), List("渋谷"), List.empty))
      searchResult.hitList.size should equal(1)
      val hit = searchResult.hitList(0)
      hit.title should equal("新規ドキュメント.txt")
      this.getValue(hit.document.documentFieldList, FIELD_CONTENT) should
        equal("東京都<mark>渋谷</mark>区恵比寿1-10-17")
    }

  }
}
