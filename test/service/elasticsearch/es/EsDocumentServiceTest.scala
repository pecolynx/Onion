package service.elasticsearch.es

import com.kujilabo.common.CommonException
import com.kujilabo.models.core.{ModelIdImplT, VariableName}
import com.typesafe.scalalogging.LazyLogging
import models.core.Title
import models.elasticsearch.{IndexName, MappingName}
import models.elasticsearch.es.EsFieldAnalyzer.EsFieldAnalyzerKuromoji
import models.elasticsearch.es.EsFieldFormat.EsFieldFormatNone
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import models.elasticsearch.es._
import models.exceptions.ModelNotFoundException
import org.scalatest._

class EsDocumentServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  val URL = "http://onion.kujilabo.com:9200"
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
  val TEST_MAPPING_NAME = new MappingName(new VariableName("test_mapping"))
  val TEST_TITLE = new Title("test_title")
  val TEST_USER_ID = 456
}

class EsDocumentServiceTest_getById extends EsDocumentServiceTest with LazyLogging {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)

    val esMapping = new EsMapping(
      TEST_MAPPING_NAME,
      //;new EsFieldList(List.empty[EsField]))
      new EsFieldList(List(new EsField(new VariableName("content"), true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji))))
    EsMappingService.addMapping(URL, TEST_INDEX_NAME, esMapping)
  }

  describe("ドキュメントが登録されているとき") {
    it("ドキュメントを取得できること") {
      this.initialize()
      val esDocument = new EsDocument(
        TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_TITLE, TEST_USER_ID,
        new EsDocumentFieldList(List.empty[EsDocumentField]))
      val id = EsDocumentService.addDocument(URL, esDocument)
      id match {
        case None => throw new CommonException("")
        case Some(x) =>
          EsDocumentService.containsDocument(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, x) should
            equal(true)

          val esDocument2 = EsDocumentService.getDocumentById(URL, TEST_INDEX_NAME,
            TEST_MAPPING_NAME, x).get
          esDocument2.title should equal(TEST_TITLE)
      }
    }
  }

  describe("ドキュメントが登録されていないとき") {
    it("Noneが返ること") {
      this.initialize()
      EsDocumentService.containsDocument(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, new ModelIdImplT[String]("xxx")) should equal(false)
      val id = EsDocumentService.getDocumentById(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, new ModelIdImplT[String]("xxx"))
      id should equal(None)
    }
  }
}

class EsDocumentServiceTest_remove extends EsDocumentServiceTest with LazyLogging {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)

    val esMapping = new EsMapping(
      TEST_MAPPING_NAME,
      //;new EsFieldList(List.empty[EsField]))
      new EsFieldList(List(new EsField(new VariableName("content"), true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji))))
    EsMappingService.addMapping(URL, TEST_INDEX_NAME, esMapping)
  }

  describe("ドキュメントが登録されているとき") {
    it("ドキュメントを削除できること") {
      this.initialize()
      val esDocument = new EsDocument(
        TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_TITLE, TEST_USER_ID,
        new EsDocumentFieldList(List.empty[EsDocumentField]))
      val id = EsDocumentService.addDocument(URL, esDocument)
      id match {
        case None => throw new CommonException("")
        case Some(x) =>
          EsDocumentService.removeDocument(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, x)
          EsDocumentService.containsDocument(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME,
            x) should equal(false)
      }
    }
  }

  describe("ドキュメントが登録されていないとき") {
    it("例外が投げられること") {
      intercept[ModelNotFoundException] {
        this.initialize()
        EsDocumentService.removeDocument(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, new ModelIdImplT[String]("xxx"))
      }
    }
  }
}


class EsDocumentServiceTest_removeByUser extends EsDocumentServiceTest with LazyLogging {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)

    val esMapping = new EsMapping(
      TEST_MAPPING_NAME,
      //;new EsFieldList(List.empty[EsField]))
      new EsFieldList(List(new EsField(new VariableName("content"), true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji))))
    EsMappingService.addMapping(URL, TEST_INDEX_NAME, esMapping)
  }

  describe("ドキュメントが登録されているとき") {
    it("ドキュメントを削除できること") {
      this.initialize()
      val esDocument = new EsDocument(
        TEST_INDEX_NAME, TEST_MAPPING_NAME, TEST_TITLE, TEST_USER_ID,
        new EsDocumentFieldList(List.empty[EsDocumentField]))
      val id = EsDocumentService.addDocument(URL, esDocument)
      id match {
        case None => throw new CommonException("")
        case Some(x) =>
          EsDocumentService.removeDocumentByUser(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME, new
              ModelIdImplT[Int](TEST_USER_ID))
          EsDocumentService.containsDocument(URL, TEST_INDEX_NAME, TEST_MAPPING_NAME,
            x) should equal(false)
      }
    }
  }
}
