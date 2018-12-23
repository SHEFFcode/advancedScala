package lectures

object AdvancedPatternMatching extends App {
  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println("The only element is head")
    case _ => // we don't do anything
  }

  /**
    * So far we know that the following are available for pattern matching:
    * - constants
    * - wildcards
    * - case classes
    * - tuples
    * - some special magic like above
    */

  class Person(val name: String, val age: Int) // you cannot make this a case class funfortunately, but want to pattern match
  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age > 29) None
      else Some((person.name, person.age)) // special method here

    def unapply(age: Int): Option[String] = Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 23)
  val greeting = bob match {
    case Person(n, a) => s"Hi my name is $n, and I am $a years old."
  }

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(greeting)
  println(legalStatus)

  /*
  The two objects below are companion objects solely for the purpose of pattern matching
  So we can use lowercase for their names
   */
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 8
  val mathProperty  = n match {
    case singleDigit() => "Single digit"
    case even() => "Even digit"
    case _ => "No property"
  }

  println(mathProperty)

}
