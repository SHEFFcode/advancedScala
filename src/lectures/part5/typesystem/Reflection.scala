package lectures.part5.typesystem

object Reflection extends App {
  // reflection + macros and quasicodes => META PROGRAMMING

  case class Person(name: String) {
    def sayMyName(): Unit = println(s"Hi my name is $name")
  }

  // 0 - import
  import scala.reflect.runtime.{universe => ru}

  // 1 - mirror
  val m = ru.runtimeMirror(getClass.getClassLoader) // get your hands on the current class loader

  // 2 - create a class object - description of the class
  val clazz = m.staticClass("lectures.part5.typesystem.Reflection.Person") // creating a class object by name

  // 3 - reflected mirror - can DO things
  val cm = m.reflectClass(clazz)

  // 4 - get the constructor
  val constructor = clazz.primaryConstructor.asMethod // I invoke the contructor

  // 5 - reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)

  // 6 - invoke the constructor
  val instance = constructorMirror.apply("John")

  println(instance) // this allows us to instantiate a dynamically computed class at runtime

  // ANOTHER USE CASE

  // I have an instance already computed
  val p = Person("Mary") // let's imagine you got this as a serialized object from the wire
  // method name I want to invoke is computed somewhere else
  val methodName = "sayMyName"
  // 1 - get the mirror, we already have
  // 2 - reflect the instance
  val reflected = m.reflect(p) // we are reflecting this class we got from the wire
  // 3 - method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  // 4 - reflect the method
  val method = reflected.reflectMethod(methodSymbol)
  // 5 - invoke the method
  method.apply()
}
