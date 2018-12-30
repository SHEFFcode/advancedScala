package excercises

import lectures.part4.implicits.TypeClasses.User

object EqualityPlayground extends App {
  trait Equal[T] {
    def eq(userA: T, userB: T): Boolean
  }

  implicit object UserEquality extends Equal[User] {
    override def eq(userA: User, userB: User): Boolean = userA.name == userB.name && userA.email == userB.email
  }

  /**
    * Excercise:
    * Implement this TC pattern for equality type class
    */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.eq(a, b)
  }
  val john = User("John", 30, "john@sheffmachine.com")


  val anotherJohn = User("AnotherJOhn", 45, "superJOhn@gmail.com")
  println(Equal(john, anotherJohn)) // this is called AD-HOC polymorphism, based on the type of comparisons, the compiler will grab the right comparator instance for the types

  /**
    * Exercise:
    *   - Improve this with an implicit conversion class:
    *     - ===(anotherValue: T)
    *     - !==(another: T)
    */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(otherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer.eq(value, otherValue)
    def !==(otherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.eq(value, otherValue)
  }

  println(john === anotherJohn) // a lot of compiler magic happens here

  /**
    * Here is what the compiler does:
    *   - john.===(anotherJohn)
    *   - Since user does not have a === method, it will try to wrap the user in something that does have that method
    *   - new TypeSafeEqual[User](john).===(anotherJohn)
    *   - Do we have an implicit equalizer of type Equal[User]? We do, so let's inject that as well
    *   - new TypeSafeEqual[User](john).===(anotherJohn)(UserEquality)
    *   - The shorter code is nicer and reminds us of JS with it's === sign
    * Another advantage is that it is TYPE SAFE:
    *   - println(john === 43) will not compile, compiler will enforce that operation can be done only on the same type
    */

}
