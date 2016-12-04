package com.kujilabo.service.elasticsearch

import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch._
import com.kujilabo.models.elasticsearch.db.DbFieldType._
import com.kujilabo.models.elasticsearch.es.EsFieldAnalyzer._
import com.kujilabo.models.elasticsearch.es.EsFieldFormat._
import com.kujilabo.models.elasticsearch.es.EsFieldType._
import com.kujilabo.models.elasticsearch.es._
import com.kujilabo.service.elasticsearch.db._
import com.kujilabo.service.elasticsearch.es._
import org.scalatest._
import scalikejdbc.ConnectionPool

class DocumentSearchServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
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

}

class DocumentSearchServiceTest_searchByKeyword extends DocumentSearchServiceTest {
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

      val searchResult = DocumentFileSearchServiceV1.search(
        ADDRESS, PORT, URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID,
        new FileSearchCondition(new Page(1, 10), List("渋谷"), List.empty))
      searchResult.hitList.size should equal(1)
      val hit = searchResult.hitList(0)
      hit.title should equal("新規ドキュメント.txt")
      this.getValue(hit.document.documentFieldList, FIELD_CONTENT) should
        equal("東京都<mark>渋谷</mark>区恵比寿1-10-17")
    }

    it("c") {
      this.initialize()

      EsDocumentService.addDocument(URL, this.createDocument_001)
      EsDocumentService.addDocument(URL, this.createDocument_002)

      Thread.sleep(1000)

      val searchResult = DocumentFileSearchServiceV1.search(
        ADDRESS, PORT, URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID,
        new FileSearchCondition(new Page(1, 10), List("abc txt"), List.empty))
      searchResult.hitList.size should equal(1)
    }

    it("拡張子で検索できること") {
      this.initialize()

      EsDocumentService.addDocument(URL, this.createDocument_001)
      EsDocumentService.addDocument(URL, this.createDocument_002)
      EsDocumentService.addDocument(URL, this.createDocument_003)

      Thread.sleep(1000)

      val searchResult = DocumentFileSearchServiceV1.search(
        ADDRESS, PORT, URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID,
        new FileSearchCondition(new Page(1, 10), List.empty, List("txt")))

      searchResult.hitList.size should equal(2)
    }

    it("Normalモードではなく検索できること1") {
      this.initialize()

      EsDocumentService.addDocument(URL, this.createDocument_001)
      EsDocumentService.addDocument(URL, this.createDocument_002)
      EsDocumentService.addDocument(URL, this.createDocument_003)
      EsDocumentService.addDocument(URL, this.createDocument_004)

      Thread.sleep(1000)

      val searchResult = DocumentFileSearchServiceV1.search(
        ADDRESS, PORT, URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID,
        new FileSearchCondition(new Page(1, 10), List("パスワード", "検索"), List.empty))

      searchResult.hitList.size should equal(1)
    }
    it("Normalモードではなく検索できること2") {
      this.initialize()

      EsDocumentService.addDocument(URL, this.createDocument_001)
      EsDocumentService.addDocument(URL, this.createDocument_002)
      EsDocumentService.addDocument(URL, this.createDocument_003)
      EsDocumentService.addDocument(URL, this.createDocument_004)

      Thread.sleep(1000)

      val searchResult = DocumentFileSearchServiceV1.search(
        ADDRESS, PORT, URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID,
        new FileSearchCondition(new Page(1, 10), List("パスワード", "検索", "ログ"), List("csv")))

      searchResult.hitList.size should equal(1)
    }
    it("Normalモードではなく検索できること3") {
      this.initialize()

      EsDocumentService.addDocument(URL, this.createDocument_001)
      EsDocumentService.addDocument(URL, this.createDocument_002)
      EsDocumentService.addDocument(URL, this.createDocument_003)
      EsDocumentService.addDocument(URL, this.createDocument_004)

      Thread.sleep(1000)

      val searchResult = DocumentFileSearchServiceV1.search(
        ADDRESS, PORT, URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID,
        new FileSearchCondition(new Page(1, 10), List("パスワード", "検索", "ログ"), List("csv2")))

      searchResult.hitList.size should equal(0)
    }
    it("自分のファイルのみ検索できること3") {
      this.initialize()

      EsDocumentService.addDocument(URL, this.createDocument_001)
      EsDocumentService.addDocument(URL, this.createDocument_002)
      EsDocumentService.addDocument(URL, this.createDocument_003)
      EsDocumentService.addDocument(URL, this.createDocument_004)
      EsDocumentService.addDocument(URL, this.createDocument_005)

      Thread.sleep(1000)

      val searchResult = DocumentFileSearchServiceV1.search(
        ADDRESS, PORT, URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_USER_ID_2,
        new FileSearchCondition(new Page(1, 10), List.empty, List.empty))

      searchResult.hitList.size should equal(1)
    }
  }
}