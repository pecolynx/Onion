package models.core

import models.exceptions.ModelValidationException
import org.scalatest._

class PageTest extends FunSpec with ShouldMatchers {
  describe("インスタンスを生成したとき") {
    it("ページ番号を取得できること") {
      new Page(1, 20).page should equal(1)
    }
    it("1ページあたりの件数を取得できること") {
      new Page(1, 20).size should equal(20)
    }
  }

  describe("ページ番号が0のとき") {
    it("例外が投げられること") {
      intercept[ModelValidationException] {
        new Page(0, 20)
      }
    }
  }
  describe("1ページあたりの件数が0のとき") {
    it("例外が投げられること") {
      intercept[ModelValidationException] {
        new Page(1, 0)
      }
    }
  }
  describe("1ページあたりの件数が1000のとき") {
    it("1ページあたりの件数を取得できること") {
      new Page(1, 1000).size should equal(1000)
    }
  }
  describe("1ページあたりの件数が1001のとき") {
    it("例外が投げられること") {
      intercept[ModelValidationException] {
        new Page(1, 1001)
      }
    }
  }
}
