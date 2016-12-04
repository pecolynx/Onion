package service

import com.kujilabo.models.core.Page
import org.scalatest._
import scalikejdbc.scalatest.AutoRollback
import org.scalatest.fixture.FunSpec
import scalikejdbc.NamedDB

class LangServiceTest extends FunSpec with AutoRollback with BeforeAndAfter with ShouldMatchers {
  scalikejdbc.config.DBs.setup('test)

  override def db = NamedDB('test).toDB
}

class DbLangServiceTest_getList extends LangServiceTest {
  describe("2件登録されているとき") {
    it("3件ずつ1ページ目を取得できること") { implicit session =>
      println("db = " + db)

      val list = LangService.getModelList(new Page(1, 3))
      list.size should equal(2)
      list(0).name should equal("日本語")
      list(1).name should equal("English")
    }
  }
}