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
}

class EmptySet[A] extends MySet[A] {

  override def contains(element: A): Boolean = false

  override def +(element: A): MySet[A] = new NonEmptySet[A](element, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet // empty plus whatever is that something else

  override def map[B](f: A => B): MySet[B] = new EmptySet[B] // mapping here would not matter, we will always get another empty

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B] // flatMapping here will return an empty set

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = () // this does not do anything, will return unit, which is ()

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

