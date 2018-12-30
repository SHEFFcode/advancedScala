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

  // part 3

  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHtml) // UserSerializer is injected by the compiler

  /**
    * We can extend the functionality to new types, cause HTML enrichment can wrap any type
    * We can choose the implementation by either importing a different serializer or passing an explicit serializer
    * Super expressive
    */

  println(2.toHTML) // here the IntSerializer is injected by the compiler

  /**
    * What do we need for this?
    *   - Type class itself # type class HTMLSerializer[T] {...}
    *   - type class instances (some of which are implicit) # UserSerializer / IntSerializer
    *   - Conversion with implicit classes # implicit class HTMLEnrichment
    */

  //context bounds
  def HTMLBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String = {
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

  def HTMLSugar[T: HTMLSerializer](content: T): String = { // this is telling the compiler to inject an implicit HTMLSerializer of type T
    /**
      * If we wanted to get the implicit serializer supplied to us by the compuler we could say:
      *   - val serializers = implicidly[HTMLSerializer] and use it around
      */
    s"<html><body>${content.toHTML}</body></html>"
  }

  // implicitly is a cute method
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of the code we want to know what the implicit value for permissions
  val standardPerms = implicitly[Permissions] // surfaces out the implicit value

}
