package lectures.part5.typesystem

object Variance extends App {
  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // What is variance?
  // Type substitution of generics
  class Cage[T] // Should a Cage[Cat] also inherit from Cage[Animal[?
  // yes? - covariance
  class CovarianceCage[+T] // this is a covariant cage, this allows us to the following:
  val ccage: CovarianceCage[Animal] = new CovarianceCage[Cat] // we are going general to specific
  // no? - invariance
  class InvariantCage[T] // cannot replace one type of Cage with another type of Cage
//  val icage: InvariantCage[Animal] = new InvariantCage[Cat] - does not work
  val icage: InvariantCage[Animal] = new InvariantCage[Animal] // works, types have to match exactly
  // HELL NO, opposite - contra variance
  class ContraCage[-T] // contra variant cage, type substitution works in the opposite direction
  val xcage: ContraCage[Cat] = new ContraCage[Animal] // this is valid, specific to general

  class ICage[T](val animal: T) // invariant

  // covariant positions
  class CCage[+T](val animal: T) // type of T is in a covariant position

//  class XCage[-T](val animal: T) // contravariant type T occurs in a covariant position
  /**
    * IF the above error did not come up, you could do
    *   - val catCage: XCage[Cat] = new XCage[Animal)(new Crocodile)
    */

  // similar logic applies to var fields
//  class CVCage[+T](var animal: T) // will not compile: covariant type T occurs in contravariant position for var

  /**
    * IF the above error did not come up, you could do:
    *   - val ccage: CCage[Animal] = new CCage[Cat](new Cat), which is fine, but you could then do
    *   - ccage.animal = new Crocodile, which would be bad
    */

//  class XVCage[-T] (var animal: T) // will also not compile

  // vars are in covariant and contravariant position, they can only be used in the invariant classes
  class IVCage[T](var animal: T) // this is ok


  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal: T) // contravarient posision
  }

  // does not work because you could write val ccage: CCAGE[Animal} = new CCage[Dog]
  // ccage.add(new Cat)

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true // this is fine
  }

  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)

  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  // this is sad cause we want to create covariant lists
  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty) // fine because Kitty is covariant, this is a list of kitties
  val moreAnimals = animals.add(new Cat) // this will become a list of Cats
  val evenMoreAnimals = moreAnimals.add(new Dog) // this is now a list of animals, the compiler has widened the type
  // ALL The elements in a list have a common type

  // method return types
  class PetShop[-T] {
//    def get(isItAPuppy: Boolean): T // compiler error Contravarient type T is at a covarient position
    /*
    val catShop = new PetShop[Animal] {
      def get(isItAPuppy: Boolean): Animal = new Cat
    }

    val dogShop == new PetShop[Dog] = catShop
    dogShop.get(true) // EVIL CAT
     */
    // solution
    def get[S <: T](isItAPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal] // allowed because PetShop is contravariant
//  val evilCat = shop.get(true, new Cat) // compiler will complain that cat does not extend Dog so this call is illegal
  class TerraNova extends Dog // this is a type of a dog
  val bigFurry = shop.get(true, new TerraNova) // this will be allowed

  /**
    * BIG RULE:
    *   - method arguments are in contra variant position
    *   - return types are in covariant position
    *
    */
}
