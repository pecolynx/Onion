package sample.ex

import org.scalatest._

sealed trait List[+A]

case object Nil extends List[Nothing]

case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {
  def sum(ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] = {
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
  }

  def tail[A](l: List[A]): List[A] = l match {
    case Nil => sys.error("")
    case Cons(_, b) => b
  }

  def setHead[A](l: List[A], a: A): List[A] = l match {
    case Nil => sys.error("")
    case Cons(_, c) => Cons(a, c)
  }

  def drop[A](l: List[A], n: Int): List[A] = {
    if (n <= 0) l
    else l match {
      case Nil => sys.error("")
      case Cons(_, a) => drop(a, n - 1)
    }
  }

  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
    case Cons(h, t) if f(h) => dropWhile(t, f)
    case _ => l
  }

  def dropWhile2[A](l: List[A])(f: A => Boolean): List[A] = l match {
    case Cons(h, t) if f(h) => dropWhile(t, f)
    case _ => l
  }

  def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B = as match {
    case Nil => z
    case Cons(x, xs) => f(x, foldRight(xs, z)(f))
  }

  def foldLeft[A, B](as: List[A], z: B)(f: (B, A) => B): B = as match {
    case Nil => z
    case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
  }

  def sum2(ints: List[Int]): Int = {
    List.foldLeft(ints, 0)((x, y) => x + y)
  }

  def product2(ds: List[Double]): Double = {
    List.foldLeft(ds, 1.0)((x, y) => x * y)
  }

  def len2(ints: List[Int]): Int = {
    List.foldLeft(ints, 0)((x, _) => x + 1)
  }

  def reverse(ints: List[Int]): List[Int] = {
    List.foldLeft(ints, List[Int]())((x, y) => Cons(y, x))
  }

  def append(ints: List[Int], z: List[Int]): List[Int] = {
    List.foldRight(ints, z)((x, y) => {
      println(x);
      Cons(x, y)
    })
  }

  def flatten(l: List[List[Int]]): List[Int] = {
    List.foldRight(l, List[Int]())((x, y) => List.append(x, y))
  }

  def map[A, B](l: List[A])(f: A => B): List[B] = {
    List.foldRight(l, List[B]())((x, y) => Cons(f(x), y))
  }

  def filter[A](l: List[A])(f: A => Boolean): List[A] = {
    List.foldRight(l, List[A]())((x, y) => if (f(x)) Cons(x, y) else y)
  }

  def zipWithSum(a: List[Int], b: List[Int]): List[Int] = (a, b) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(x1, y1), Cons(x2, y2)) => Cons(x1 + x2, zipWithSum(y1, y2))
  }

  def zipWith[A, B, C](a: List[A], b: List[B])(f: (A, B) => C): List[C] = (a, b) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(x1, y1), Cons(x2, y2)) => Cons(f(x1, x2), zipWith(y1, y2)(f))
  }

}

class Sample003 extends FunSpec with ShouldMatchers {
  describe("コンストラクタ") {
    describe("名前が20文字のとき") {
      it("名前を取得できること") {
        List.tail(List(1, 2, 3)) should equal(List(2, 3))
        List.setHead(List(1, 2, 3), 4) should equal(List(4, 2, 3))
        List.drop(List(1, 2, 3, 4, 5), 3) should equal(List(4, 5))
        List.dropWhile(List(2, 4, 5, 4), (a: Int) => a % 2 == 0) should equal(List(5, 4))
        List.dropWhile2(List(2, 4, 5, 4))(a => a % 2 == 0) should equal(List(5, 4))
        List.foldRight(List(2, 3, 4), 1) { (x, y) => x + y } should equal(10)
        List.foldRight(List(2, 3, 4), 1)(_ + _) should equal(10)
        List.foldRight(List(0, 0, 0), 0)((x, y) => y + 1) should equal(3)
        List.foldLeft(List(2, 3, 4), 1)(_ + _) should equal(10)
        List.sum2(List(1, 2, 3)) should equal(6)
        List.product2(List(1, 2, 3, 4)) should equal(24)
        List.len2(List(1, 2, 3)) should equal(3)
        List.reverse(List(1, 2, 3)) should equal(List(3, 2, 1))
        List.append(List(1, 2, 3), List(4, 5, 6)) should equal(List(1, 2, 3, 4, 5, 6))
        List.flatten(List(List(1, 2), List(3, 4))) should equal(List(1, 2, 3, 4))
        List.foldRight(List(1, 2), List[Int]())((x, y) => Cons(x + 1, y)) should equal(List(2, 3))
        List.foldRight(List(1, 2), List[String]())((x, y) => Cons(x.toString, y)) should equal(List
        ("1", "2"))
        List.map(List(1, 2))(x => x * 2) should equal(List(2, 4))
        List.filter(List(1, 2, 3, 4))(x => x % 2 == 0) should equal(List(2, 4))
        List.zipWithSum(List(1, 2), List(3, 4)) should equal(List(4, 6))
      }
    }
  }

}
