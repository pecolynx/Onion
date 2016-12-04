package com.kujilabo.models.elasticsearch

import com.kujilabo.models.core.VariableName
import com.kujilabo.validation.ModelValidationException
import org.scalatest._

class IndexNameTest extends FunSpec with ShouldMatchers {
  describe("コンストラクタ") {
    describe("名前が20文字のとき") {
      val value = "12345678901234567890"
      val indexName = new IndexName(new VariableName(value))

      it("名前を取得できること") {
        indexName.value.value should equal(value)
      }
      it("文字列表現を取得できること") {
        indexName.toString should equal(value)
      }
    }

    describe("名前が0文字のとき") {
      val value = ""

      it("例外が投げられること") {
        intercept[ModelValidationException] {
          new IndexName(new VariableName(value))
        }
      }
    }

    describe("名前が21文字のとき") {
      val value = "123456789012345678901"

      it("例外が投げられること") {
        intercept[ModelValidationException] {
          new IndexName(new VariableName(value))
        }
      }
    }
  }

  describe("equals") {
    describe("等しいとき") {
      it("trueが返ること") {
        new IndexName(new VariableName("abc")) ==
          new IndexName(new VariableName("abc")) should equal(true)
      }
    }
    describe("等しくないとき") {
      it("falseが返ること") {
        new IndexName(new VariableName("abc")) ==
          new IndexName(new VariableName("def")) should equal(false)
      }
    }
    describe("型が異なるとき") {
      it("falseが返ること") {
        new IndexName(new VariableName("abc")) == "abc" should equal(false)
      }
    }
  }
}