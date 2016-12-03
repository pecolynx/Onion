package com.kujilabo.util

import org.scalatest._

class IntegerUtilsTest extends FunSpec with ShouldMatchers {
  describe("toInt") {
    describe("引数がnullのとき") {
      it("Noneが返ること") {
        IntegerUtils.toInt(null) should equal(None)
      }
    }
    describe("引数がabcのとき") {
      it("Noneが返ること") {
        IntegerUtils.toInt("abc") should equal(None)
      }
    }
    describe("引数が123.456のとき") {
      it("Noneが返ること") {
        IntegerUtils.toInt("123.456") should equal(None)
      }
    }
    describe("引数が 123 のとき") {
      it("Noneが返ること") {
        IntegerUtils.toInt(" 123 ") should equal(None)
      }
    }
    describe("引数が123のとき") {
      it("Some(123)が返ること") {
        IntegerUtils.toInt("123") should equal(Some(123))
      }
    }
  }
}
