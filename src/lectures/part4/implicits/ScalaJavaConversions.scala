package lectures.part4.implicits

import collection.JavaConverters._
import java.{util => ju}
import collection.mutable._

object ScalaJavaConversions extends App {
  val javaSet = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala

  /**
    * There is a bunch of conversion method
    *   - Iterators
    *   - Iterable
    *   - ju.List => collection.mutable.Buffer
    *   - ju.Set => collection.mutable.Set
    *   - ju.Map => collection.mutable.Map
    */

  val numberBuffer = ArrayBuffer[Int](1, 2, 3)
  val juNumbersBuffer = numberBuffer.asJava

  println(juNumbersBuffer.asScala eq numberBuffer) // this will return true, points back to the original Scala object

  val numbersList = List(1, 2, 3) // this is an ummutable type
  val juNumbers = numbersList.asJava // java list is mutable, but it will throw an error because it SHOULD be immutable
  val backToScala = juNumbers.asScala // this will not give back the same type, because numbers are immutable

  println(backToScala eq numbersList) // shallow is false
  println(backToScala == numbersList) // deep is true

  /**
    * Exercise: Your own java to scala conversion for optional to scala Option
    *
    *
    */

  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = {
    new ToScala[Option[T]](if (o.isPresent) Some(o.get) else None)
  }

  val juOptional: ju.Optional[Int] = ju.Optional.of(2)
  val scalaOption = juOptional.asScala // wraps the call into asScalaOptional(juOptional).asScala
  println(scalaOption)
}
