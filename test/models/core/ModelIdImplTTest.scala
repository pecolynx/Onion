package models.core

import com.kujilabo.models.core.ModelIdImplT
import org.scalatest._

class ModelIdImplTTest extends FunSpec with BeforeAndAfter with ShouldMatchers {

  describe("ModelIdImplT#コンストラクタ") {
    describe("引数が正しいとき") {
      val target = new ModelIdImplT[Int](123)

      it("値を取得できること") {
        target.value should equal(123)
      }

      it("文字列表現を取得できること") {
        target.toString() should equal("123")
      }

      it("値で比較できること") {
        target should equal(new ModelIdImplT[Int](123))
      }
    }
  }
}