package com.kujilabo.service.elasticsearch

import com.kujilabo.common.ModelAlreadyExistsException
import com.kujilabo.models.core.VariableName
import com.kujilabo.models.elasticsearch.{Index, IndexName}
import com.kujilabo.service.elasticsearch.db.DbIndexService
import com.kujilabo.service.elasticsearch.es.EsIndexService
import org.scalatest._
import scalikejdbc.ConnectionPool

class IndexServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  val URL = "http://onion.kujilabo.com:9200"
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
  before {
    val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)
  }
}

class IndexServiceTest_add extends IndexServiceTest {
  def initialize(): Unit = {
    if (DbIndexService.containsModel(TEST_INDEX_NAME)) {
      val dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
      DbIndexService.removeModel(dbIndex.id, dbIndex.version)
    }

    if (EsIndexService.containsIndex(URL, TEST_INDEX_NAME)) {
      EsIndexService.removeIndex(URL, TEST_INDEX_NAME)
    }
  }

  describe("存在しないインデックスを追加したとき") {
    it("追加されること") {
      this.initialize()
      IndexService.addIndex(URL, new Index(TEST_INDEX_NAME))
      IndexService.containsIndex(URL, TEST_INDEX_NAME) should equal(true)
    }
  }
  describe("存在するインデックスを追加したとき") {
    it("例外が投げられること") {
      intercept[ModelAlreadyExistsException] {
        this.initialize()
        IndexService.addIndex(URL, new Index(TEST_INDEX_NAME))
        IndexService.addIndex(URL, new Index(TEST_INDEX_NAME))
      }
    }
  }
}
