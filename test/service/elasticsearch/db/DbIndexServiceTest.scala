package service.elasticsearch.db

import com.kujilabo.models.core.{ModelIdImplT, VariableName}
import models.core.{Page}
import models.elasticsearch.IndexName
import models.elasticsearch.db.DbIndex
import models.elasticsearch.db.DbIndexBuilder
import models.exceptions.ModelNotFoundException
import org.joda.time.DateTime
import org.scalatest._
import scalikejdbc.{ConnectionPool, NamedDB}
import org.scalatest.fixture.FunSpec
import scalikejdbc.scalatest.AutoRollback

class DbIndexServiceTest extends FunSpec with AutoRollback with BeforeAndAfter with ShouldMatchers {
  override def db = NamedDB('test).toDB

  scalikejdbc.config.DBs.setup('test)

  before {
    println("***before***")
    val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)
  }

  after {
    println("***after***")
  }

}

class DbIndexServiceTest_getList extends DbIndexServiceTest {
  def initialize(size: Int): Unit = {
    DbIndexService.removeAll()
    for (i <- 0 to 5 - 1) {
      DbIndexService.addModel(new DbIndexBuilder().withName(i.toString()).build)
    }
  }

  describe("5件登録されているとき") {
    it("3件ずつ1ページ目を取得できること") { implicit session =>
      this.initialize(5)
      val list = DbIndexService.getModelList(new Page(1, 3))
      list.size should equal(3)
    }

    it("3件ずつ2ページ目を取得できること") { implicit session =>
      this.initialize(5)
      val list = DbIndexService.getModelList(new Page(2, 3))
      list.size should equal(2)
    }
  }
}

class DbIndexServiceTest_getByKey extends DbIndexServiceTest {
  def initialize() = {
    DbIndexService.removeAll()
    DbIndexService.addModel(new DbIndexBuilder().withName("a").build)
  }

  describe("データが登録されているとき") {
    it("取得できること") { implicit session =>
      this.initialize()
      val model = DbIndexService.getModelByKey(new IndexName(new VariableName("a")))
      model.name.value.value should equal("a")
    }
  }
  describe("データが登録されていないとき") {
    it("例外が投げられること") { implicit session =>
      intercept[ModelNotFoundException] {
        DbIndexService.getModelByKey(new IndexName(new VariableName("z")))
      }
    }
  }
}

class DbIndexServiceTest_remove extends DbIndexServiceTest {
  def initialize() {
    DbIndexService.removeAll()
    DbIndexService.addModel(new DbIndexBuilder().withName("a").build)
  }

  describe("データが登録されているとき") {
    it("削除できること") { implicit session =>
      this.initialize()
      val model = DbIndexService.getModelByKey(new IndexName(new VariableName("a")))
      DbIndexService.removeModel(model.id, model.version)
      DbIndexService.containsModel(new IndexName(new VariableName("a"))) should equal(false)
    }
  }

  describe("データが登録されていないとき") {
    it("例外が投げられること") { implicit session =>
      intercept[ModelNotFoundException] {
        DbIndexService.removeModel(new ModelIdImplT[Int](-1), 0)
      }
    }
  }
}

class DbIndexServiceTest_update extends DbIndexServiceTest {
  def initialize() {
    DbIndexService.removeAll()
    DbIndexService.addModel(new DbIndexBuilder().withName("a").build)
  }

  describe("データが登録されているとき") {
    it("更新できること") { implicit session =>
      this.initialize()
      val model1 = DbIndexService.getModelByKey(new IndexName(new VariableName("a")))

      val model2 = new DbIndex(
        model1.id, model1.version, model1.createdAt, model1.updatedAt,
        new IndexName(new VariableName("new_name")))
      DbIndexService.updateModel(model2)

      val model3 = DbIndexService.getModelByKey(new IndexName(new VariableName("new_name")))
      model3.name.value.value should equal("new_name")
    }
  }

  describe("データが登録されていないとき") {
    it("例外が投げられること") { implicit session =>
      intercept[ModelNotFoundException] {
        val model = new DbIndex(
          new ModelIdImplT[Int](-1), 0, new DateTime(), new DateTime(),
          new IndexName(new VariableName("new_name")))
        DbIndexService.updateModel(model)
      }
    }
  }
}
