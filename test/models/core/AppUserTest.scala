package models.core

import models.exceptions.ModelValidationException
import org.scalatest._

class AppUserTest extends FunSpec with ShouldMatchers {

  describe("AppUser#Constructor_Idなし") {
    describe("正しい引数のとき") {
      val target = new AppUser("loginid", "loginpass", "email", "田中")
      it("名前を取得できること") {
        target.name should equal("田中")
      }
    }
    describe("不正な引数のとき") {
      it("ログインIdが21文字のとき例外が投げられること") {
        intercept[ModelValidationException] {
          val value = "123456789012345678901"
          val target = new AppUser(value, "loginpass", "email", "田中")
        }
      }
    }
  }
}