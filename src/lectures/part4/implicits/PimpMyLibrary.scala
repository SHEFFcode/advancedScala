package lectures.part4.implicits

object PimpMyLibrary extends App {
  // 2.isPrime is what we are trying to achieve
  implicit class RichInt(value: Int) { // implicit classes can only take one argument
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      def timesHelper(n: Int): Unit = {
        if (n <= 0) ()
        else {
          function()
          timesHelper(n - 1)
        }
      }
      timesHelper(value)// run it that many times
    }

    def *[T](list: List[T]): List[T] = {
      def concatinate(n: Int): List[T] = {
        if (n <= 0) List[T]()
        else {
          concatinate(n - 1) ++ list
        }
      }
      concatinate(value)
    }
  }

  // we can do this...
  val richInt = new RichInt(42).sqrt // this is all well and good
  42.isEven // we can also do this!!! which is wicked cool!
  // ^ this is called type enrichment, or pimping

  /**
    * Exercises:
    *   - Enrich the string class asInt
    *   - Encrypt, which does the caesar cypher
    *   - Keep enriching the Int class with 2 methods
    *     - times
    *     - multiply
    */

  implicit class RichString(string: String) {
    def asInt: Int = Integer.valueOf(string) // this returns a java.lang.Integer, which scala then converts to an int
    def encrypt(cypherDistance: Int): String = string.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  println("3".asInt + 4)// should see 7
  println("John".encrypt(2)) // Lqjp

  3.times(() => println("Scala rocks!!"))
  println(4 * List(1, 2, 3))

  // Can we do automatic string to int conversion like JS? YES
  // "3" / 4
  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
  println("6" / 2) // compiles and prints 3!

  // equivalent to an implementation of an implicit class
  class RichAlternativeInt(value: Int) {
    implicit def enrich(value: Int): RichAlternativeInt = new RichAlternativeInt(value) // this way of doing things is discauraged

    // danger zone
    implicit def intToBoolean(i: Int): Boolean = i == 1
    //goal is if (i) do something, else do something else

    val conditionedValue = if (3) "Ok" else "Something wrong"
    println(conditionedValue) // if there is a bug with implicit conversion with a method its hard to trace it back
  }

  /**
    * Rules of the road with pimping:
    *   - keep type enrichment to implicit classes or type classes
    *   - avoid implicit defs where possible
    *   -
    */
}
