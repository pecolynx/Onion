package service.elasticsearch.es

import models.core.VariableName
import models.elasticsearch.{IndexName, MappingName}
import models.elasticsearch.es.EsFieldAnalyzer.EsFieldAnalyzerKuromoji
import models.elasticsearch.es.EsFieldFormat.EsFieldFormatNone
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import models.elasticsearch.es.{EsField, EsFieldList, EsMapping}
import models.exceptions.ModelNotFoundException
import org.scalatest._

class EsMappingServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  val URL = "http://onion.kujilabo.com:9200"
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
  val TEST_MAPPING_NAME = new MappingName(new VariableName("test_mapping"))
  val FILE_PATH_FIELD_NAME = new VariableName("file_path")
}

class EsMappingServiceTest_get extends EsMappingServiceTest {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)

    val esMapping = new EsMapping(
      TEST_MAPPING_NAME,
      new EsFieldList(List(
        new EsField(FILE_PATH_FIELD_NAME, true, true, true,
          EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji)
      )))
    EsMappingService.addMapping(URL, TEST_INDEX_NAME, esMapping)
  }

  describe("マッピングが存在するとき") {
    it("マッピングを取得できること") {
      this.initialize()
      val esMapping = EsMappingService.getMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME)
      esMapping.name should equal(TEST_MAPPING_NAME)

      val esField = esMapping.fieldList.get(FILE_PATH_FIELD_NAME)
      esField.fieldType should equal(EsFieldTypeString)
    }
  }
  describe("マッピングが存在しないとき") {
    it("例外が投げられること") {
      intercept[ModelNotFoundException] {
        EsMappingService.getMapping(URL, TEST_INDEX_NAME, new MappingName(new VariableName("x")))
      }
    }
  }
}

class EsMappingServiceTest_contains extends EsMappingServiceTest {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)

    val esMapping = new EsMapping(
      TEST_MAPPING_NAME,
      new EsFieldList(List.empty[EsField]))
    EsMappingService.addMapping(URL, TEST_INDEX_NAME, esMapping)
  }

  describe("マッピングが存在するとき") {
    it("trueが返却されること") {
      this.initialize()
      EsMappingService.containsMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME) should equal(true)
    }
  }
  describe("マッピングが存在しないとき") {
    it("falseが返却されること") {
      EsIndexService.containsIndex(URL, new IndexName(new VariableName("zzz"))) should equal(false)
    }
  }
}


class EsMappingServiceTest_remove extends EsMappingServiceTest {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)

    val esMapping = new EsMapping(
      TEST_MAPPING_NAME,
      new EsFieldList(List.empty[EsField]))
    EsMappingService.addMapping(URL, TEST_INDEX_NAME, esMapping)
  }

  describe("マッピングが存在するとき") {
    it("削除できること") {
      this.initialize()
      EsMappingService.removeMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME)
      EsMappingService.containsMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME) should equal(false)
    }
  }
  describe("マッピングが存在しないとき") {
    it("例外が投げられること") {
      intercept[ModelNotFoundException] {
        EsMappingService.removeMapping(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME)
      }
    }
  }
}