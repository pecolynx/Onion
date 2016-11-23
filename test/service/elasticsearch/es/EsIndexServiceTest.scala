package service.elasticsearch.es

import models.core.VariableName
import models.elasticsearch.IndexName
import models.exceptions.ModelNotFoundException
import org.scalatest._

class EsIndexServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  val URL = "http://onion.kujilabo.com:9200"
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
}

class EsIndexServiceTest_contains extends EsIndexServiceTest {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)
  }

  describe("インデックスが存在するとき") {
    it("trueが返却されること") {
      EsIndexService.containsIndex(URL, TEST_INDEX_NAME) should equal(true)
    }
  }
  describe("インデックスが存在しないとき") {
    it("falseが返却されること") {
      EsIndexService.containsIndex(URL, new IndexName(new VariableName("zzz"))) should equal(false)
    }
  }
}

class EsIndexServiceTest_add extends EsIndexServiceTest {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }
  }

  describe("") {
    it("") {
      this.initialize()
      EsIndexService.addIndex(URL, TEST_INDEX_NAME)
    }
  }
}

class EsIndexServiceTest_remove extends EsIndexServiceTest {
  def initialize(): Unit = {
    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }

    EsIndexService.addIndex(URL, TEST_INDEX_NAME)
  }

  describe("インデックスが存在するとき") {
    it("削除できること") {
      this.initialize()
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
      EsIndexService.containsIndex(URL, TEST_INDEX_NAME) should equal(false)
    }
  }
  describe("インデックスが存在しないとき") {
    it("例外が投げられること") {
      this.initialize()
      intercept[ModelNotFoundException] {
        EsIndexService.removeIndex(URL, new IndexName(new VariableName("zzz")))
      }
    }
  }
}
