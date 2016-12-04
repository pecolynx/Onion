package sample

import org.scalatest.{FunSpec, ShouldMatchers}

import scala.util.{Failure, Success, Try}

class SampleTest003 extends FunSpec with ShouldMatchers {

  def func_001(b: Boolean): Option[String] = {

    val i = if (b) Some(100) else None
    i.map((x) => x.toString)

    //    i match {
    //      case None => return None
    //      case Some(x) => return Some(x.toString())
    //    }
  }

  describe("a") {
    it("falseの場合") {
      func_001(false) should equal(None)
    }
    it("trueの場合") {
      func_001(true) should equal(Some("100"))
    }
  }

  def writeFile(a: Int): Try[Unit] = Try {
    if (a % 2 == 0) throw new RuntimeException("x")
  }

  describe("b") {
    it("c") {
      val result: Try[Unit] = for {
        a <- writeFile(1)
        b <- writeFile(2)
      } yield ()

      result match {
        case Success(_) => println("Success!")
        case Failure(t) => println(s"Failure! ${t.getMessage}")
      }
    }
  }
}
