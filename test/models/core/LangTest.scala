package models.core

import models.exceptions.ModelValidationException
import org.scalatest._

class LangTest extends FunSpec with ShouldMatchers {
  describe("インスタンスを生成したとき") {
    it("言語コードを取得できること") {
      new Lang("ja", "日本語").code should equal("ja")
    }
    it("名前を取得できること") {
      new Lang("ja", "日本語").name should equal("日本語")
    }
  }

  describe("言語コードが空文字のとき") {
    it("例外が投げられること") {
      intercept[ModelValidationException] {
        new Lang("", "日本語")
      }
    }
  }

  describe("名前が空文字のとき") {
    it("例外が投げられること") {
      intercept[ModelValidationException] {
        new Lang("ja", "")
      }
    }
  }
}
