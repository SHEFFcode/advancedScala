package lectures.part1.beginning

import scala.util.Try

object DarkSugars extends App {
  // Syntax sugar #1: Methods with single params
  def singleArgumentMethod(arg: Int) : String = s"$arg little ducks"
  val description = singleArgumentMethod {
    // write some code
    42
  }

  val aTryInstance = Try {
    throw new RuntimeException("I am a try with an exception applied to it")
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // sugar # 2: instances of traits with single method can be reduced to single abstract method
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunkyInstance: Action = (x: Int) => x + 1 // a lot of magic here to see that this conforms to trait Action

  //example runnable - instances of a trait or java intrerface can be passed on to thread
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello scala!")
  })

  val aSweeterThread = new Thread(() => println("Sweet scala")) // scala is better here

  abstract class AbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  val anAbstractInstance: AbstractType = (a: Int) => println("Sweet")

  //sugar # 3: the :: and #:: operators
  val prependedList = 2 :: List(3, 4) // compiler rewrites this as List(3, 4).::(2) ?!
  /**
    * Scala spec: Last character decides associativity of method, so if it is a colon, it is right associative
    * So operation on the right actually gets performed first in this case
    */

  class MyStream[T] {
    def -->: (value: T): MyStream[T] = this // actual implementation goes here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  //sugar #4: multi-word method naming
  class TeenGirl(name: String) {
    def `and then said`(gossip: String): Unit = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala so sweet!"

  // sugar # 5: infix types
  class Composite[A, B]
  val composite: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> Int = ???

  //sugar #6: update method, is very special like apply
  val anArray  = Array(1, 2, 3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7), very used in mutable collections

  //Sugar#6: setters
  class Mutable {
    private var internalMember: Int = 0
    def member = internalMember // getter
    def member_=(value: Int): Unit = {
      internalMember = value // setter
    }

    val aMutableContainer = new Mutable
    aMutableContainer.member = 42 // rewritten as aMutableContainer.member_=(42)

  }



}
