package excercises

import scala.annotation.tailrec

/**
  * Exercise
  * 1. Implement a lazy evaluated, singly linked stream of values
  *   - val naturals = MyStream.from(1)(x => x + 1)  => this will become a stream of natural numbers (potentially infinite )
  *   - naturals.take(100) => lazily evaluated stream of the first 100 naturals (this is a finite stream, but still lazily evaluated)
  *   - naturals.map(_ * 2) // stream of all even numbers (potentially infinite)
  */

abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[A] // prepend operator, this is a variance problem
  def ++:[B >: A](anotherStream: MyStream[B]): MyStream[B] // another covariance problem, concats two streams

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements out of the stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc
    else tail.toList(head :: acc)

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = new Cons[A](start, MyStream.from(generator(start))(generator))
  }
}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException

  override def tail: MyStream[Nothing] = throw new NoSuchElementException

  override def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this) // prepend operator

  override def ++:[B >: Nothing](anotherStream: MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def map[B](f: Nothing => B): MyStream[B] = this

  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this

}

class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false

  override val head: A = hd // as a val, because we might want to reuse it around the body

  override lazy val tail: MyStream[A] = tl // this is called call by need (combining call by name with lazy val)

  override def #::[B >: A](element: B): MyStream[A] = new Cons[A](element, this)

  override def ++:[B >: A](anotherStream: MyStream[B]): MyStream[B] = new Cons[B](head, tail ++ anotherStream)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def map[B](f: A => B): MyStream[B] = new Cons[B](f(head), tail.map(f))

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): MyStream[A] = {
    if (predicate(head)) new Cons[A](head, tail.filter(predicate))
    else tail.filter(predicate)
  }

  override def take(n: Int): MyStream[A] = {
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons[A](head, EmptyStream)
    else new Cons(head, tail.take(n - 1))
  }

}

object StreamsPlayground extends App {

}
