package lectures.part4.implicits

object ImplicitsIntro extends App {
  val pair = "Daniel" -> "55"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi my name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet) // although the greet method is part of the person class, this will compile and work
  // this becomes println(fromStringToPerson("Peter").greet)
  // If there is more than one implicit that can get you to something with the same method name, the compiler won't compile

  // Implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10

  increment(10) // if we omit the second param, we will default to the default value
}
