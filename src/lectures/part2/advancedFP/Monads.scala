package lectures.part2.advancedFP

/**
  * This is our own Try monad
  */

object Monads extends App {
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] = {
      try {
        Success(a) // if we can resolve the value, we will return a success
      } catch {
        case e: Throwable => Fail(e) // if we get into an error we will return a failure
      }
    }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
      try {
        f(value)
      } catch {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Fail[+A](value: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this // failure of flatMap anything will return a failure
  }

  /**
    * See if we succeed in the laws:
    * 1. Left Identity - unit.flatMap(f) = f(x)
    *   - In our case, Attempt(x).flatMap(f) = f(x) // Makes sense for Success case only
    * 2. Right Identity - attempt.flatMap(unit) = attempt
    *   - In our case, Success(x).flatMap(x => Attempt(x)) = Attempt(x) || Success(x)
    * 3. Associativity - attempt.flatMap(f).flatMap(g) = attempt.flatMap(x => f(x).flatMap(g))
    *   - In our case, Fail(e).flatMap(f).flatMap(g) => Fail(e) == Fail(e).flatMap(x => f(x).flatMap(g)) => Fail(e) so this was easy
    *   - For success, Success(x).flatMap(f).flatMap(g) = f(v).flatMap(g) OR Fail(e) == Success(v).flatMap(x => f(x).flatMap(g)) = f(v).flatMap(g) OR Fail(e)
    */

  val attempt = Attempt {
    throw new RuntimeException("My own MONAD, YES!!!")
  }

  println(attempt)

  /**
    * Exercise - implement a lazy monad, which will only be evaluated when its needed
    * You need two methods:
    *   - Apply
    *   - FlatMap
    */

  class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value

    def use: A = internalValue
    def flatMap[B](f: (=>A) => Lazy[B]): Lazy[B] = f(internalValue) // here the function receives the parameter by name as well
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value) // here instead of running any code we just return another instance
  }

  val lazyInstance = Lazy {
    println("Today I don't feel like doing anything")
    42
  }

  println(lazyInstance.use)

  val flatMappedInstance = lazyInstance.flatMap(x => Lazy { x * 10 }) // do see the lyric here because the application of lazy is applied eagerly, but the value itself s not evaluated, only the function
  val flatMappedInstance2 = lazyInstance.flatMap(x => Lazy { x * 10 }) // do see the lyric here because the application of lazy is applied eagerly, but the value itself s not evaluated, only the function

  flatMappedInstance.use // we call the evaluation twice here, which is bad, we should use call by need
  flatMappedInstance2.use


  /**
    * Exercise:
    * Monads can be thought of as apply + flatMap, but also as apply, map and flatten
    * Think about how to transform one into another
    */

  /**
    * def flatMap[B](f: T => Monad[B]): Monad[B] = ... (implemented)
    * def map[B](f: T => B): Monad[B] = flatMap(x => unit(f(x))) // which is a Monad[B] so an extra function to wrap in a monad
    * def flatten(m: Monad[Monad[T]]): Monad[T] = m.flatMap((x: Monad[T]) => x)
    *
    * List(1, 2, 3).map(_ * 2) = List(1, 2, 3).flatMap(x => List(x * 2))
    * List(List(1, 2), List(3, 4)).flatten = List(List(1, 2), List(3, 4)).flatMap(x => x) = List(1, 2, 3, 4)
    */
}
