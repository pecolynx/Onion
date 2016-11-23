package sample.ex

import org.scalatest._

sealed trait Option[+A] {
  def map[B](f: A => B): Option[B] = this match {
    case None => None
    case Some(x) => Some(f(x))
  }

  def flatMap[B](f: A => Option[B]): Option[B] = this match {
    case None => None
    case Some(x) => f(x)
  }

  def getOrElse[B >: A](default: => B): B = this match {
    case None => default
    case Some(x) => x
  }

  def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
    case None => ob
    case Some(x) => Some(x)
  }

  def filter(f: A => Boolean): Option[A] = this match {
    case Some(x) if f(x) => this
    case _ => None
  }
}

case class Some[+A](get: A) extends Option[A]

case object None extends Option[Nothing]

class Sample004 extends FunSpec with ShouldMatchers {
  def mean(xs: Seq[Double]): Option[Double] = {
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)
  }

  def parseInsuranceRateQuote(age: String, numberOfSpeedingTickets: String): Option[Double] = {
    val optAge: Option[Int] = Try(age.toInt)
    val optTickets: Option[Int] = Try(numberOfSpeedingTickets.toInt)
    map3(optAge, optTickets)(insuranceRateQuote)
  }

  def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = (a, b) match {
    case (Some(x), Some(y)) => Some(f(x, y))
    case _ => None
  }

  def map3[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = {
    a.flatMap(aa => b.map(bb => f(aa, bb)))
  }

  def insuranceRateQuote(a: Int, b: Int): Double = {
    0.0
  }

  def Try[A](a: => A): Option[A] = {
    try Some(a)
    catch {
      case a: Exception => None
    }
  }

  def sequence[A](a: scala.collection.immutable.List[Option[A]]):
  Option[scala.collection.immutable.List[A]] = a match {
    case scala.collection.immutable.Nil => Some(scala.collection.immutable.Nil)
    case a :: b => a flatMap (aa => sequence(b) map (aa :: _))


  }

  describe("コンストラクタ") {
    describe("a") {
      it("b") {
        mean(scala.collection.immutable.List(1.0, 2.0)) should equal(Some(1.5))
        Some(10).map(x => x * 2) should equal(Some(20))
        Some(10).getOrElse(20) should equal(10)
        None.getOrElse(20) should equal(20)

        Some(10).filter((x => x % 2 == 0)) should equal(Some(10))
        Some(9).filter((x => x % 2 == 0)) should equal(None)

        parseInsuranceRateQuote("10", "20") should equal(Some(0.0))
        parseInsuranceRateQuote("abc", "20") should equal(None)
      }
    }
  }
}
