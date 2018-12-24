package lectures.part2.advancedFP

object PartialFunctions extends App {
  val aFunction = (x: Int) => x + 1// this is a Function1[Int, Int] or Int => Int
  val afussyFunction = (x: Int) => { // function only accepts certain values
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 3) 999
    else throw new RuntimeException("Function not implemented")
  }

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 3 => 999
    case _ => throw new RuntimeException("Funtion not impelmented for this value")
  }

  // This is a partial function from int to int, or more appropriately {1, 2, 3} => Int

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 3 => 999
  } // partial function value, this is a bit shorter then the function above with the match

  println(aPartialFunction(2))

  /**
    * Partial function utlities:
    * - isDefinedAt
    */

  aPartialFunction.isDefinedAt(67) // very useful test
  // lifted
  val lifted = aPartialFunction.lift // this will return a regular function from INnt => Option[Int]
  println(lifted(2))
  println(lifted(98))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  // PF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  //HOFs accept partial function as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }

  println(aMappedList)

  /**
    * PF Can only have one parameter type
    */


  /**
    * 1. Construct a partial function as an anon class
    * 2. dumb chatbot as a PF
    */


  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(v1: Int): Int = v1 match {
      case 1 => 42
      case 2 => 65
      case _ => 999
    }
    override def isDefinedAt(x: Int): Boolean =
      x == 1 ||
      x == 2 ||
      x == 5
  }

  val chatbox: PartialFunction[String, String] = {
    case "hello" => "Hi, my name is HAL9000"
    case "goodbye" => "One you start talking to me there is no return human"
    case "call mom" => "Unable to find your phone without your credit card"
  }

  scala.io.Source.stdin.getLines().foreach(line => println("chatbot said: " + chatbox(line)))

}
