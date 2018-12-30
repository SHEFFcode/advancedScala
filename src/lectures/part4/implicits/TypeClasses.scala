package lectures.part4.implicits

object TypeClasses extends App {
  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email /> </div>"
  }

  val john = User("John", 30, "john@sheffmachine.com")
  /**
    * Issues:
    * 1) Only works for the types we write
    * 2) ONE implementation out of quite a number
    */

  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
//      case java.util.Date =>
      case _ =>
    }
  }

  /**
    * Issues:
    * 1) lost type safety
    * 2) need to mod the code every time
    * 3) still one implementation for each given type
    */

  trait HTMLSerializer[T] { // this is called a type class
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] { // this is called a type class instance
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email} /> </div>"
  }

  /**
    * Befits of the above design:
    * 1) we can define serializers for other types
    * 2) We can define mutliple serializers for a certain type
    */

  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  /**
    * In general:
    *   - Type class looks like a trait with a type param + a bunch of actions
    *
    */

  println(UserSerializer.serialize(john))

  /**
    * Excercise
    * Equality type class, has a type eq that compares two values
    */

  trait Equal[T] {
    def eq(userA: T, userB: T): Boolean
  }

  implicit object UserEquality extends Equal[User] {
    override def eq(userA: User, userB: User): Boolean = userA.name == userB.name && userA.email == userB.email
  }

  // Implicits and Type classes
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = {
      serializer.serialize(value)
    }

    def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div>$value</div>"
  }

  println(HTMLSerializer.serialize(42)) // we don't need to supply the serializer here, because we have an implicit one for ints above
  println(HTMLSerializer.serialize(john)) // also implicit here with the user serializer
  println(HTMLSerializer[User].serialize(john)) // this is even better because now we have access to other methods aside from serialize

  /**
    * Excercise:
    * Implement this TC pattern for equality type class
    */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.eq(a, b)
  }

  val anotherJohn = User("AnotherJOhn", 45, "superJOhn@gmail.com")
  println(Equal(john, anotherJohn)) // this is called AD-HOC polymorphism, based on the type of comparisons, the compiler will grab the right comparator instance for the types
}
