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

}
