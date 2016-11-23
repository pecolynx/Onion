package models.elasticsearch

import models.core.VariableName
import models.exceptions.ModelValidationException
import org.scalatest._


class MappingNameTest extends FunSpec with ShouldMatchers {
  describe("コンストラクタ") {
    describe("名前が20文字のとき") {
      it("名前を取得できること") {
        val value = "12345678901234567890"
        val mappingName = new MappingName(new VariableName(value))
        mappingName.value.value should equal(value)
      }
      it("文字列表現を取得できること") {
        new MappingName(new VariableName("abc")).toString should equal("abc")
      }
    }

    describe("名前が0文字のとき") {
      it("例外が投げられること") {
        intercept[ModelValidationException] {
          val value = ""
          new MappingName(new VariableName(value))
        }
      }
    }

    describe("名前が21文字のとき") {
      it("例外が投げられること") {
        intercept[ModelValidationException] {
          val value = "123456789012345678901"
          new MappingName(new VariableName(value))
        }
      }
    }
  }

  describe("equals") {
    describe("等しいとき") {
      it("trueが返ること") {
        new MappingName(new VariableName("abc")) ==
          new MappingName(new VariableName("abc")) should equal(true)
      }
    }
    describe("等しくないとき") {
      it("falseが返ること") {
        new MappingName(new VariableName("abc")) ==
          new MappingName(new VariableName("def")) should equal(false)
      }
    }
    describe("型がことなるとき") {
      it("falseが返ること") {
        new MappingName(new VariableName("abc")) == "abc" should equal(false)
      }
    }
  }
}
