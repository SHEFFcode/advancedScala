package lectures.part5.typesystem

object FBoundedPolymorphism extends App {
//  trait Animal {
//    def breed(): List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed(): List[Animal] = ??? // we would like for this to be a List of Cats
//  }
//
//  class Dog extends Animal {
//    override def breed(): List[Animal] = ??? // we would like for this to be a List of Dogs
//  }

  // Solution 1 - naive

//  trait Animal {
//    def breed(): List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed(): List[Cat] = ??? // we would like for this to be a List of Cats
//  }
//
//  class Dog extends Animal {
//    override def breed(): List[Dog] = ??? // we would like for this to be a List of Dogs
//  }

  // Solution 2 - F Bounded Polymorphism

//  trait Animal[A <: Animal[A]] { // recursive type, animal appears in its own type signature. F-bounded polymorphism
//    def breed(): List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed(): List[Animal[Cat]] = ??? // we would like for this to be a List of Cats
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed(): List[Animal[Dog]] = ??? // we would like for this to be a List of Dogs
//  }
//
//  // ^ this is userd in ORM
//  trait Entity[E <: Entity[E]] // used a lot in ORMs
//
//  class Person extends Comparable[Person] { // also F-Bounded polymorphism
//    override def compareTo(o: Person): Int = ???
//  }
//
//  // ^ the above is good, but
//  class Crocodile extends Animal[Dog] {
//    override def breed(): List[Animal[Dog]] = ??? // This is a Dog not a crocodile!
//  }

  // Solution # 3 FBP + self-types

  trait Animal[A <: Animal[A]] { self: A => // recursive type, animal appears in its own type signature. F-bounded polymorphism
    def breed(): List[Animal[A]]
  }

  class Cat extends Animal[Cat] {
    override def breed(): List[Animal[Cat]] = ??? // we would like for this to be a List of Cats
  }

  class Dog extends Animal[Dog] {
    override def breed(): List[Animal[Dog]] = ??? // we would like for this to be a List of Dogs
  }

//  class Crocodile extends Animal[Dog] { // compiler will complain!!!
//    override def breed(): List[Animal[Dog]] = ??? // This is a Dog not a crocodile!
//  }

  trait Fish extends Animal[Fish]
  class Shark extends Fish {
    override def breed(): List[Animal[Fish]] = List(new Cod) // this is wrong
  }
  class Cod extends Fish {
    override def breed(): List[Animal[Fish]] = ???
  }


}
