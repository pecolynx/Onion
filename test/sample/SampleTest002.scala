package sample

import org.scalatest._

class SampleTest002 extends FunSpec with ShouldMatchers {
  describe("コンストラクタ") {
    describe("名前が20文字のとき") {
      it("a") {
        List(1, 2, 3).foldLeft(0) { (x, y) => x + y } should equal(6)
      }

      it("b") {
        val a = None
        val b = Some(1)
        var z = 0
        a.map { (x: Int) => z = z + x }
        b.map { (x: Int) => z = z + x }
        z should equal(1)


      }
    }
  }

}
