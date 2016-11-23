package models

import models.core.{AppUser, AppUserBuilder, ModelIdImplT, Page}
import models.exceptions.ModelNotFoundException
import org.joda.time.DateTime
import org.scalatest._
import scalikejdbc.ConnectionPool
import services.AppUserService

class AppUserServiceTest extends FunSpec with BeforeAndAfter with ShouldMatchers {
  before {
    val settings = scalikejdbc.config.DBs.readJDBCSettings('test)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)
    AppUserService.removeAll()
  }
}

class AppUserServiceTest_getList extends AppUserServiceTest {
  def initialize(size: Int) = {
    for (i <- 0 to size - 1) {
      AppUserService.addModel(new AppUserBuilder().withLoginId(i.toString()).build)
    }
  }

  describe("5件登録されているとき") {
    it("3件ずつ1ページ目を取得できること") {
      this.initialize(5)
      val appUserList = AppUserService.getModelList(new Page(1, 3))
      appUserList.size should equal(3)
    }

    it("3件ずつ2ページ目を取得できること") {
      this.initialize(5)
      val appUserList = AppUserService.getModelList(new Page(2, 3))
      appUserList.size should equal(2)
    }
  }
}

class AppUserServiceTest_getByKey extends AppUserServiceTest {
  def initialize() = {
    AppUserService.addModel(new AppUserBuilder().withLoginId("a").build)
  }

  describe("データが登録されているとき") {
    it("取得できること") {
      this.initialize()
      val appUser = AppUserService.getModelByKey("a").get
      appUser.loginId should equal("a")
    }
  }
  describe("データが登録されていないとき") {
    it("例外が投げられること") {
      AppUserService.getModelByKey("z") should equal(None)
    }
  }
}

class AppUserServiceTest_remove extends AppUserServiceTest {
  def initialize() {
    AppUserService.addModel(new AppUserBuilder().withLoginId("a").build)
  }

  describe("データが登録されているとき") {
    it("削除できること") {
      this.initialize()
      val appUser = AppUserService.getModelByKey("a").get
      AppUserService.removeModel(appUser.id, appUser.version)
      AppUserService.containsModel("a") should equal(false)
    }
  }

  describe("データが登録されていないとき") {
    it("例外が投げられること") {
      intercept[ModelNotFoundException] {
        AppUserService.removeModel(new ModelIdImplT[Int](-1), 0)
      }
    }
  }
}

class AppUserServiceTest_update extends AppUserServiceTest {
  def initialize() {
    AppUserService.addModel(new AppUserBuilder().withLoginId("a").build)
  }

  describe("データが登録されているとき") {
    it("更新できること") {
      this.initialize()
      val appUser1 = AppUserService.getModelByKey("a").get

      val appUser2 = new AppUser(
        appUser1.id, appUser1.version, appUser1.createdAt, appUser1.updatedAt,
        "new_loginId", "new_loginPass",
        "new_email", "new_name")
      AppUserService.updateModel(appUser2)

      val appUser3 = AppUserService.getModelByKey("new_loginId").get
      appUser3.loginId should equal("new_loginId")
      //appUser3.loginPassword should equal("new_loginPass")
      appUser3.email should equal("new_email")
      appUser3.name should equal("new_name")
    }
  }

  describe("データが登録されていないとき") {
    it("例外が投げられること") {
      intercept[ModelNotFoundException] {
        val appUser2 = new AppUser(
          new ModelIdImplT[Int](-1), 0, new DateTime(), new DateTime(),
          "new_loginId", "new_loginPass",
          "new_email", "new_name")
        AppUserService.updateModel(appUser2)
      }
    }
  }
}
