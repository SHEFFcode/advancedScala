package lectures.part2.advancedFP

trait MySet[A] extends (A => Boolean) {

  /**
    * Excercise 1- implement a functional set
    */

  def apply(el: A): Boolean = contains(el)

  def contains(element: A): Boolean
  def +(element: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  /**
    * Exercise:
    * - removing an element
    * - intersection with another set
    * - difference with another set
    */
  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A] // difference
  def &(anotherSet: MySet[A]): MySet[A] // intersection
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {

  override def contains(element: A): Boolean = false

  override def +(element: A): MySet[A] = new NonEmptySet[A](element, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet // empty plus whatever is that something else

  override def map[B](f: A => B): MySet[B] = new EmptySet[B] // mapping here would not matter, we will always get another empty

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B] // flatMapping here will return an empty set

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = () // this does not do anything, will return unit, which is ()

  override def -(elem: A): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  // Say you are given Set(1, 2, 3), return everything but that set

  override def unary_! : MySet[A] = new AllInclusiveSet[A]
}

class AllInclusiveSet[A] extends MySet[A] {
  override def contains(element: A): Boolean = true // because it has all the values in theory

  override def +(element: A): MySet[A] = this // same reason as above

  override def ++(anotherSet: MySet[A]): MySet[A] = this // same as above

  /**
    * Imagine I have val naturals =  allInclusiveSet[Int] = all real numbers
    * What happens when I say naturals map _ % 3 => ???
    * [0, 1, 2] from an infinite set we went to a finite set
    * We don't know yet how to implement this, so we will leave it alone for now
    */
  override def map[B](f: A => B): MySet[B] = ???
  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???
  override def foreach(f: A => Unit): Unit = ???

  override def filter(predicate: A => Boolean): MySet[A] = ??? // Property based set

  override def -(elem: A): MySet[A] = ???

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new EmptySet[A]
}

/**
  * Property based set is a set of all elements of type A which satisfy a property
  * In math terms:
  * { x in A | property(x) }
  */
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(element: A): Boolean = property(element) // the set contains this item if the property holds

  /**
    * What do we get when we add an element to a property based set
    * { x in A | property(x) } + element = { x in A | property(x) || x == element }
    */
  override def +(element: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == element)

  /**
    * What do we get when we add an element to a property based set
    * { x in A | property(x) } ++ anotherSet = { x in A | property(x) || anotherSet contains x }
    */
  override def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  /**
    * We are avoiding for now
    */
  override def map[B](f: A => B): MySet[B] = politerlyFail
  override def flatMap[B](f: A => MySet[B]): MySet[B] = politerlyFail
  override def foreach(f: A => Unit): Unit = politerlyFail

  /**
    * A set where the property and the predicate hold for x
    */
  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(elem: A): MySet[A] = filter(_ != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x)) // we are flipping the predicate (property) here

  /**
    * This is the case for map and flatMap, you don't know quite what will come out on the other side
    * If you man an infinite set, you will get a set, but you will not know if that set is finite or not, which breaks the whole point of a set
    */
  def politerlyFail = throw new IllegalArgumentException("Really deep rabbit hole!") // for flat and
}


class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(element: A): Boolean = element == head || tail.contains(element)

  override def +(element: A): MySet[A] =
    if (this.contains(element)) this // if we already have the element, we cannot add any more items to it
    else new NonEmptySet[A](element, this)

  /**
    * [1, 2, 3] ++ [4, 5]
    * [2, 3] ++  [4, 5] + 1
    * [3] ++ [4, 5] + 1 + 2
    * [] ++ [4, 5] + 1 + 2 + 3 here we reference the implementation for empty set, which for ++ returns another set
    * [4, 5] + 1 + 2 + 3, which is not a simple + operation so we get
    * [3, 2, 1, 4, 5] // we don't care about the ordering, just the contract of the Set is met
    */
  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head // funky because of recursion and polymorphism

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head) // so apply f to the tail and add in the f(head) to the list

  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head // because the head passes the predicate, so we add it to the result
    else filteredTail // else we don't add it to the result
  }

  override def foreach(f: A => Unit): Unit = {
    f(head) // apply f to head
    tail foreach f // call f on the tail recursively
  }
  override def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head // we will run a recursive operation here on the tail each time, while putting head back

  override def --(anotherSet: MySet[A]): MySet[A] = this filter !anotherSet

  override def &(anotherSet: MySet[A]): MySet[A] = this filter anotherSet // intersection and filter is the same thing

  // new operator
  override def unary_! : MySet[A] = {
    new MySet[A] {
      override def contains(element: A): Boolean = ???

      override def +(element: A): MySet[A] = ???

      override def ++(anotherSet: MySet[A]): MySet[A] = ???

      override def map[B](f: A => B): MySet[B] = ???

      override def flatMap[B](f: A => MySet[B]): MySet[B] = ???

      override def filter(predicate: A => Boolean): MySet[A] = ???

      override def foreach(f: A => Unit): Unit = ???

      /**
        * Exercise:
        * - removing an element
        * - intersection with another set
        * - difference with another set
        */
      override def -(elem: A): MySet[A] = ???

      override def --(anotherSet: MySet[A]): MySet[A] = ???

      override def &(anotherSet: MySet[A]): MySet[A] = ???

      override def unary_! : MySet[A] = ???
    }
  }
}

object MySet {
  def apply[A](values: A*): MySet[A] = {
    /**
      * val s = new MySet(1, 2, 3) = buildSet(seq(1, 2, 3), [])
      * buildSet(seq(2, 3), [] + 1)
      * buildSet(seq(3), [1] + 2)
      * buildSet(seq(), [1 , 2] + 3
      * return [1, 2, 3]
      */
    def buildSet(valSeq: Seq[A], accumulator: MySet[A]): MySet[A] = {
      if (valSeq.isEmpty) accumulator // if we are not adding anything, we will return the accumulator
      else buildSet(valSeq.tail, accumulator + valSeq.head) // if we are adding something, we will add valSeq to accum and call build set on the rest of the sequence
    }
    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, x * 10)) filter (_ % 2 == 0) foreach println
}

