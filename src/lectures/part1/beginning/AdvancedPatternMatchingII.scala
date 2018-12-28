package lectures.part1.beginning

object AdvancedPatternMatchingII extends App {
  // Infix patterns
  case class Or[A, B](a: A, b: B) // In scala this pattern is called either
  val either = Or(2, "Two")
  val humanDescription = either match {
//    case Or(number, string) => s"$number is written as $string"
    case number Or string => s"$number is written as $string" // this is identical to the code above,
  }

  println(humanDescription)

  // Decomposing sequences
  val numbers = List(1, 2, 3)
  val varArgs  = numbers match {
    case List(1, _*) => "starting with 1" // pattern matching for alist, we need an aunapply sequence
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  // Custom return types for unapply
  // isEmpty: Boolean, get: Something
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

}
