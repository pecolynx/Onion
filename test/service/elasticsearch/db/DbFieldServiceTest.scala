package service.elasticsearch.db

import com.kujilabo.models.core.{ModelIdImplT, VariableName}
import models.core.Page
import models.elasticsearch.{IndexName, MappingName}
import models.elasticsearch.db.{DbFieldBuilder, DbIndexBuilder, DbMapping, DbMappingBuilder}
import org.scalatest._
import org.scalatest.fixture.FunSpec
import scalikejdbc.{ConnectionPool, NamedDB}
import scalikejdbc.scalatest.AutoRollback

class DbFieldServiceTest extends FunSpec with AutoRollback with BeforeAndAfter with ShouldMatchers {
  val TEST_INDEX_NAME = new IndexName(new VariableName("test_index"))
  val TEST_MAPPING_NAME = new MappingName(new VariableName("test_mapping"))
  val MULTI_LANG_NAME = "名前"
  var dbMapping: DbMapping = _
  before {
    println("***before***")
    val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)
  }

  after {
    println("***after***")
  }

}

class DbFieldServiceTest_getList extends DbFieldServiceTest {
  def initialize(size: Int): Unit = {
    DbIndexService.removeAll()
    DbIndexService.addModel(new DbIndexBuilder().withName(TEST_INDEX_NAME).build)
    val dbIndex = DbIndexService.getModelByKey(TEST_INDEX_NAME)
    DbMappingService.addModel(new DbMappingBuilder().withIndexId(dbIndex.id).withName(TEST_MAPPING_NAME).build)
    dbMapping = DbMappingService.getModelByKey(dbIndex.id, TEST_MAPPING_NAME)

    for (i <- 0 to size - 1) {
      DbFieldService.addModel(
        new DbFieldBuilder()
          .withMappingId(dbMapping.id)
          .withName(i.toString)
          .withSeq(i + 1)
          .withMultiLangName(MULTI_LANG_NAME + i.toString)
          .build,
        new ModelIdImplT[Int](1))
    }
  }

  describe("5件登録されているとき") {
    it("5件取得できること") { implicit session =>
      this.initialize(5)
      val list = DbFieldService.getModelList(dbMapping.id)
      list.size should equal(5)
    }
    it("名前を取得できること") { implicit session =>
      this.initialize(5)
      val list = DbFieldService.getModelList(dbMapping.id)
      list(0).multiLangName should equal("名前0")
      list(1).multiLangName should equal("名前1")
      list(2).multiLangName should equal("名前2")
      list(3).multiLangName should equal("名前3")
      list(4).multiLangName should equal("名前4")
    }
  }
}