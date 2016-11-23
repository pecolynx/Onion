package service.elasticsearch

import models.core.VariableName
import models.elasticsearch._
import models.elasticsearch.db.DbFieldType.DbFieldTypeText
import models.elasticsearch.es.EsFieldAnalyzer.EsFieldAnalyzerKuromoji
import models.elasticsearch.es.EsFieldFormat.EsFieldFormatNone
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import models.exceptions.ModelAlreadyExistsException
import org.scalatest._
import scalikejdbc.ConnectionPool
import service.elasticsearch.db.{DbFieldService, DbIndexService, DbMappingService}
import service.elasticsearch.es.EsIndexService

class MappingServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  val URL = "http://onion.kujilabo.com:9200"
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
  val TEST_MAPPING_NAME = new MappingName(new VariableName("test_mapping"))
  val FIELD_FILE_PATH = new VariableName("file_path")
  val FIELD_FILE_NAME = new VariableName("file_name")
  val FIELD_CONTENT = new VariableName("content")
  var index: Index = null
  before {
    val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)
  }
}

class MappingServiceTest_add extends MappingServiceTest {
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
  }

  describe("存在しないマッピングを追加したとき") {
    it("追加されること") {
      this.initialize()
      MappingService.addMapping(URL, TEST_INDEX_NAME, new Mapping(index, TEST_MAPPING_NAME))
      MappingService.containsMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME) should equal(true)
    }
  }
  describe("存在するマッピングを追加したとき") {
    it("例外が投げられること") {
      intercept[ModelAlreadyExistsException] {
        this.initialize()
        MappingService.addMapping(URL, TEST_INDEX_NAME, new Mapping(index, TEST_MAPPING_NAME))
        MappingService.addMapping(URL, TEST_INDEX_NAME, new Mapping(index, TEST_MAPPING_NAME))
      }
    }
  }
}

class MappingServiceTest_update extends MappingServiceTest {
  def initialize(): Unit = {
    if (DbIndexService.containsModel(TEST_INDEX_NAME)) {
      val dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
      DbIndexService.removeModel(dbIndex.id, dbIndex.version)
    }

    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    IndexService.addIndex(URL, new Index(TEST_INDEX_NAME))
    MappingService.addMapping(URL, TEST_INDEX_NAME, new Mapping(index, TEST_MAPPING_NAME))
  }

  describe("マッピングにフィールドを追加したとき") {
    it("フィールドが追加されること") {
      this.initialize()
      val index = IndexService.getIndex(URL, TEST_INDEX_NAME)
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
      MappingService.containsMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME) should equal(true)
      val mapping2 = MappingService.getMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME)

      val list = DbFieldService.getModelList(mapping2.id)
      list.size should equal(3)
      list(0).name should equal(FIELD_FILE_PATH)
      list(1).name should equal(FIELD_FILE_NAME)
      list(2).name should equal(FIELD_CONTENT)
    }
  }

}