package lectures.part4.implicits

object OrganizingImplicits extends App {
  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _) // now greater then means less then
  println(List(1, 2, 3, 4).sorted) // sorted takes an implicit ordering value, this comes from scala.Predef, which is auto imported
  // we now see the items in reverse order, because my implicit overrides the one in scala.Predef

  /**
    * Implicits (used as implicit params) can be:
    *   - val / var
    *   - object
    *   - accessor methods (defs with no parans)
    */

  case class Person(name: String, age: Int)

  implicit val ordering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0) // string comparison scala

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  println(persons.sorted)

  /**
    * Implicit scope
    *   - normal scope = LOCAL SCOPE
    *   - imported scope = import *
    *   - companion objects of all the types involved in the method signature
    * Bast Practices:
    *   - Define the implicits in the companion objects
    *   - If there are different equally likely implicits, package them separately and have the user import them
    */

  /**
    * Exercise: Create three orderings
    *   - totalPrice
    *   - by unit count
    *   - by unit price
    */
  case class Purchase(nUnits: Int, unitPrice: Double)
  object Purchase {
    implicit val purchaseOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
  }

  object UnitCountPurchaseOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object PricePurchaseOrdering {
    implicit val priceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }

}
