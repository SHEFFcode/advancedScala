package lectures.part2.advancedFP

object CurriesAndPartiallyAppliedFunctions extends App {
  /**
    * Curried functions, they return other functions as their result
    */

  val superAdder: Int => Int => Int = x => y => x + y // this is a curried function, where x returns a function that then adds x and y
  val add3 = superAdder(3) // this is same as val add3: Int => Int = y => 3 + y
  println(add3(2)) // should give us 5

  // This is a METHOD
  def curriedAdder(x: Int)(y: Int): Int = x + y // this is a curried method
  val add4: Int => Int = curriedAdder(4) // this adds one param predefined to the function, this WILL NOT work without the type annotation
  // ^ this is called lifting or ETA - EXPANSION, this is done by the compiler to turn functions into methods
  // FUNCTIONS are NOT METHODS, this is a JVM limitation

  def inc(x: Int) = x + 1
  List(1, 2, 3).map(inc) // Compiler does the ETA-EXPANSION for us and rewrites this as (x => inc(x))

  /**
    * Partial function applications
    */
  val add5 = curriedAdder(5) _ // hey compiler, do an ETA-expansion for me and return the result after applying five, make this an Int => Int function

  /**
    * Exercises:
    * 1. Define add7: Int => Int = y => 7 + y
    *   - have as many implementations as you possibly can, be creative
    */

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int): Int = x + y

  val add7Functional = (y: Int) => simpleAddFunction(7, y)
  val add7Functional_2 = simpleAddFunction.curried(7) // functions have curried method apparently
  val add7_6 = simpleAddFunction(7, _: Int) // this is an alternative syntax similar to add7_5
  val add7Method = (y: Int) => simpleAddMethod(7, y)
  val add7Curried = curriedAddMethod(7) _ // ETA Expansion partially applied function
  val add7Curried_another = curriedAddMethod(7)(_) // this is doing the same as above
  val add7_5 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values y => simpleAddMethod(7, y)

  println(add7Functional(1))
  println(add7Method(1))
  println(add7Curried(1))

  /**
    * Underscores are powerful
    */
  def concactinator(a: String, b: String, c: String) = a + b + c
  val insertName = concactinator("Hello, I am ", _: String, ", how are you?") // I am converting this method into a function
  //^ above is x: String => concactinator("Hello, I am ", x, ", how are you?")
  println(insertName("Jeremy"))

  val fillInBlanks = concactinator("Hello, ", _: String, _: String) // we are doing super eta expansion here
  println(fillInBlanks("Jeremy", " Scala is awesome!"))

  /**
    * Exercises:
    * 1. Process a list of numbers and return their string representations with different formats
    *   - Use: %4.2f, %8.6f, %14.12f with a curried formatter function
    * 2. Dive into the difference between
    *   - Functions vs Methods
    *   - Parameters: by name vs 0-lambda
    */
  println("%4.2f".format(Math.PI))

  def formatter(number: Double, formtr: String): String = formtr.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  val fourPointTwoFormatter = formatter(_: Double, "%4.2f")
  val eightPointSixFormatter = formatter(_: Double, "%8.6f")
  val fourteenPointTwelveFormatter = formatter(_: Double, "%14.12f")

  numbers.map(fourPointTwoFormatter).foreach(println) // there is actually an eta expansion for println here

  println(fourPointTwoFormatter(Math.PI))
  println(eightPointSixFormatter(Math.PI))
  println(fourteenPointTwelveFormatter(Math.PI))

  def byName(n: => Int): Int = n + 1
  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42 // this is an accessor method, compiler will not do ETA expansion
  def paramMethod(): Int = 42 // this is a proper method, compiler will do ETA expansion

  /**
    * Call by name and by function
    *   - Int
    *   - method
    *   - paramMethod()
    *   - lambda
    *   - partially applied function
    *
    *  Which cases compile and which do not?
    */

  byName(42) // ok
  byName(method) // ok, because method will be evaled to 42
  byName(paramMethod())
  byName(paramMethod) // this is also ok, but beware, this method is actually called by the compiler => byName(paramMethod())
//  byName(() => byName(42)) // this is not the same as => Int so this is not ok
  byName((() => 42)()) // IFFE is ok here
//  byName(paramMethod _) // is not ok, because by name does not take a function

//  byFunction(45) // this does not work because 45 is not a function
//  byFunction(method) // a parameterless method is not ok, because the method becomes 42, and is not a function, no ETA expansion
  byFunction(paramMethod) // compiler will do eta expansion
  byFunction(() => 46) // this also works
  byFunction(paramMethod _) // this will also work, because we are getting that sweet ETA expansion, but warning, unnecessary _ see above
}
