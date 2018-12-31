package lectures.part5.typesystem

object StructuralTypes extends App {
  type JavaClosable = java.io.Closeable
  class HipsterCloseable {
    def close(): Unit = println("yea yea im closing")
  }

//  def closeQuietly(closeable: JavaClosable OR HipsterCloseable) // ?!
  type UnifiedCloseable = {
    def close(): Unit
  } // structural type

  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaClosable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable) // both methods work fine with our duck type

  // Type refinements
  type advancedCloseable = JavaClosable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaClosable {
    override def close(): Unit = println("Java closses")
    def closeSilently() = println("Java closes silently")
  }

  def closeShh(advancedJavaCloseable: AdvancedJavaCloseable): Unit = advancedJavaCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)

  // Using structural types as standalone types
  def altClose(closeable: { def close(): Unit}): Unit = closeable.close() // we have TS like types!

  // type checking = duck typing
  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("woof")
  }

  class Car {
    def makeSound(): Unit = println("Zoom")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // CAVEAT: based on reflection, and reflective calls have a big impact on performance

}
