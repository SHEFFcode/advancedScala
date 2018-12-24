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

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(element: A): Boolean = element == head || tail.contains(element)

  override def +(element: A): MySet[A] =
    if (this.contains(element))
    else new NonEmptySet[A](element, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head // funky because of recursion and polymorphism

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }
}


