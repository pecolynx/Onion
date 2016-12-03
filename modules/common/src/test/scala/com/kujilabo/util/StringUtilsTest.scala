package com.kujilabo.util

import org.scalatest._

class StringUtilsTest extends FunSpec with ShouldMatchers {
  describe("isBlank") {
    describe("引数がnullのとき") {
      it("trueが返ること") {
        StringUtils.isBlank(null) should equal(true)
      }
    }
    describe("引数が空文字のとき") {
      it("trueが返ること") {
        StringUtils.isBlank("") should equal(true)
      }
    }
    describe("引数がスペースのとき") {
      it("falseが返ること") {
        StringUtils.isBlank(" ") should equal(false)
      }
    }
  }
}
