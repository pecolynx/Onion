package com.kujilabo.service.elasticsearch.db

import com.kujilabo.common.ModelNotFoundException
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch._
import com.kujilabo.models.elasticsearch.db._
import org.joda.time.DateTime
import org.scalatest._
import scalikejdbc.ConnectionPool

class DbMappingServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
  before {
    println("***before***")
    val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)
    DbMappingService.removeAll()
    DbIndexService.removeAll()
  }
}

class DbMappingServiceTest_getList extends DbMappingServiceTest {
  var dbIndex: DbIndex = _

  def initialize(size: Int) = {
    DbIndexService.addModel(new DbIndexBuilder().withName(TEST_INDEX_NAME).build)
    dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)

    for (i <- 0 to size - 1) {
      DbMappingService.addModel(new DbMappingBuilder().withIndexId(dbIndex.id).withName("a").build)
    }
  }

  describe("5件登録されているとき") {
    it("3件ずつ1ページ目を取得できること") {
      this.initialize(5)
      val list = DbMappingService.getModelList(new Page(1, 3))
      list.size should equal(3)
    }

    it("3件ずつ2ページ目を取得できること") {
      this.initialize(5)
      val list = DbMappingService.getModelList(new Page(2, 3))
      list.size should equal(2)
    }
  }
}

class DbMappingServiceTest_getByKey extends DbMappingServiceTest {
  var dbIndex: DbIndex = _

  def initialize() = {
    DbIndexService.addModel(new DbIndexBuilder().withName(TEST_INDEX_NAME).build)
    dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
    DbMappingService.addModel(new DbMappingBuilder().withIndexId(dbIndex.id).withName("a").build)
  }

  describe("データが登録されているとき") {
    it("取得できること") {
      this.initialize()
      val model = DbMappingService.getModelByKey(dbIndex.id, new MappingName(new VariableName("a")))
      model.name.value.value should equal("a")
    }
  }
  describe("データが登録されていないとき") {
    it("例外が投げられること") {
      intercept[ModelNotFoundException] {
        this.initialize()
        DbMappingService.getModelByKey(dbIndex.id, new MappingName(new VariableName("z")))
      }
    }
  }
}

class DbMappingServiceTest_remove extends DbMappingServiceTest {
  var dbIndex: DbIndex = _

  def initialize() {
    DbIndexService.addModel(new DbIndexBuilder().withName(TEST_INDEX_NAME).build)
    dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
    DbMappingService.addModel(new DbMappingBuilder().withIndexId(dbIndex.id).withName("a").build)
  }

  describe("データが登録されているとき") {
    it("削除できること") {
      this.initialize()
      val model = DbMappingService.getModelByKey(dbIndex.id, new MappingName(new VariableName("a")))
      DbMappingService.removeModel(model.id, model.version)
      DbMappingService.containsModel(dbIndex.id, new MappingName(new VariableName("a"))) should equal(false)
    }
  }

  describe("データが登録されていないとき") {
    it("例外が投げられること") {
      intercept[ModelNotFoundException] {
        DbMappingService.removeModel(new ModelIdImplT[Int](-1), 0)
      }
    }
  }
}

class DbMappingServiceTest_update extends DbMappingServiceTest {
  var dbIndex: DbIndex = _

  def initialize() {
    DbIndexService.addModel(new DbIndexBuilder().withName(TEST_INDEX_NAME).build)
    dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
    DbMappingService.addModel(new DbMappingBuilder().withIndexId(dbIndex.id).withName("a").build)
  }

  describe("データが登録されているとき") {
    it("更新できること") {
      this.initialize()
      val model1 = DbMappingService.getModelByKey(dbIndex.id, new MappingName(new VariableName("a")))

      val model2 = new DbMapping(
        model1.id, model1.version, model1.createdAt, model1.updatedAt,
        dbIndex.id,
        new MappingName(new VariableName("new_name")))
      DbMappingService.updateModel(model2)

      val model3 = DbMappingService.getModelByKey(dbIndex.id, new MappingName(new VariableName("new_name")))
      model3.name.value.value should equal("new_name")
    }
  }

  describe("データが登録されていないとき") {
    it("例外が投げられること") {
      this.initialize()
      intercept[ModelNotFoundException] {
        val model = new DbMapping(
          new ModelIdImplT[Int](-1), 0, new DateTime(), new DateTime(),
          dbIndex.id,
          new MappingName(new VariableName("new_name")))
        DbMappingService.updateModel(model)
      }
    }
  }
}

class DbMappingServiceTest_removeCascading extends DbMappingServiceTest {
  var dbIndex: DbIndex = _

  def initialize() {
    DbIndexService.addModel(new DbIndexBuilder().withName(TEST_INDEX_NAME).build)
    dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
    DbMappingService.addModel(new DbMappingBuilder().withIndexId(dbIndex.id).withName("a").build)

  }

  describe("インデックスを削除したとき") {
    it("マッピングも削除されること") {
      this.initialize()
      DbIndexService.removeAll()
      println("removed")
      val list = DbMappingService.getModelList(new Page(1, 10))
      list.foreach(x => println(x.indexId))
      list.isEmpty should equal(true)
    }
  }
}
