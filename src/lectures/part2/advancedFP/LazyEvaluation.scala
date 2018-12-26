package lectures.part2.advancedFP

import java.util.function.Predicate

object LazyEvaluation extends App {
  /**
    * Lazy delays the evaluation of the values
    */
  lazy val x: Int = throw new RuntimeException // obviously the program will crash here, so x will evaluate only when I attempt to access it

//  println(x) // this will now crash the program

  lazy val y: Int = {
    println("Hello")
    42
  }

  println(y) // so still keeps it's use as val, it will only be evaled twice
  println(y) // it will run print Hello here

  // examples of implications
  def sideEffectCondition: Boolean = {
    println("Boo") // side effect
    true
  }

  def simpleCondition: Boolean = false // no side effect

  lazy val lazyCondition = sideEffectCondition

  println(if (simpleCondition && lazyCondition) "yes" else "no") // here sideeffect will not be printed, lazy val is not evaluated until it is needed, it is no needed here

  // In Conjunction with call by name
  def byNameMethod(n: => Int): Int = n + n + n
  def anotherByNameMethod(n: => Int): Int = {
    // this is called CALL BUY NEED
    lazy val t = n // lazy vals are only evaluated once
    t + t + t
  }
  def retrieveMagicValue= {
    // side effect with a long computation
    println("I am waiting for 1000 milliseconds")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue)) // here we will wait for 3000 milliseconds, because we will eval magicValue 3 times
  println(anotherByNameMethod(retrieveMagicValue)) // this will not wait for 3k ms, but only for 1k ms

  /**
    * Filtering with lazy vals
    */
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30) // ETA expansion here, this will return List(1, 25, 5, 23)
  val gt20 = lt30.filter(greaterThan20)

  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30) // withFilter is a function on collections that uses lazy values under the hood
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println("========================================")
  println(gt20Lazy) // we print the resulting collection, but the filtering has not happened yet
  gt20Lazy.foreach(println) // will force the filtering to take place
  //^ the above will actually check both predicates as the same time, reducing the amount of time needed for evaluation

  /**
    * For comprehensions actually use withFilter, which means that they are lazy evaluated.
    *
    */

  for {
    a <- List(1, 2, 3) if a % 2 == 0 // if guards use lazy vals
  } yield a + 1

  // this is the same as
  List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1)  // List[Int]

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
    def takeAsList(n: Int): List[A]

    object MyStream {
      def from[A](start: A)(generator: A => A): MyStream[A] = ???
    }
  }


}
