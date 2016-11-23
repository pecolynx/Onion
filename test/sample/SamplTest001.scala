package sample

import org.scalatest._

class SamplTest001 extends FunSpec with ShouldMatchers {
  describe("コンストラクタ") {
    describe("名前が20文字のとき") {
      it("a") {
        findFirst(Array("abc", "def", "ghi"), "def") should equal(1)
        val f = (a: Int, b: Int) => a * b
        val c = curry(f)
        c(2)(3) should equal(6)
      }
    }
  }

  def findFirst[A](ss: Array[A], key: A): Int = {
    def loop(n: Int): Int =
      if (n >= ss.length) -1
      else if (ss(n) == key) n
      else loop(n + 1)

    loop(0)
  }

  def curry[A, B, C](f: (A, B) => C): A => (B => C) = {
    (a: A) => (b: B) => f(a, b)
  }

  def uncurry[A, B, C](f: A => B => C): (A, B) => C = {
    (a: A, b: B) => f(a)(b)
  }

  def compose[A, B, C](f: B => C, g: A => B): A => C = {
    (a: A) => f(g(a))
  }
}
