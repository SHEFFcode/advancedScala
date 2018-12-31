package lectures.part5.typesystem

object RockingInheritance extends App {
  // Convenience
  trait Wriater[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Wriater[T] with Closeable) = {
    stream.foreach(println)
    stream.close(1)
  }

  // Diamond Problem
  trait Animal {
    def name: String
  }
  trait Lion extends Animal {
    override def name: String = "Lion"
  }
  trait Tiger extends Animal {
    override def name: String = "Tiger"
  }

//  class Mutant extends Lion with Tiger {
//    override def name: String = "Mutant" // perfectly compileable code
//  }

  class Mutant extends Lion with Tiger // code compiles even if u remove the override

  val mutant = new Mutant
  println(mutant.name) // Tiger

  /**
    * The compiler thinks like this:
    *   - Mutant extends Animal with { override def name: String = "Lion" } with { override def name: String = "Tiger" }
    *   - Last override always gets picked is how scala resolves the diamond problem
    */

  // Super problem or type linearization

  trait Cold {
    def print: Unit = println("Cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("Green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("Blue")
      super.print
    }
  }

  class Red {
    def print: Unit = println("Red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("White")
      super.print
    }
  }

  val color = new White
  color.print // ?! Prints everything BUT red!

}
