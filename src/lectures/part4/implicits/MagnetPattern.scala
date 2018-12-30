package lectures.part4.implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {
  // looks at solving some of the problems created by method overloading

  case class P2PRequest()
  case class P2PResposne()
  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResposne): Int
    def receive(future: Future[P2PRequest]): Int
//    def receive(future: Future[P2PResposne]): Int // this is not possible due to type erasure
    def receive[T : Serializer](message: T): Int
    def receive[T : Serializer](message: T, statusCode: Int): Int
    // long story short: lots of overloads
  }

  /**
    * Issues:
    *   1. Type erasure
    *   2. Lifting does not work for all overloads # val recieveFV = receive _ , underscore of what?, compiler confused
    *   3. Code duplication
    *   4. Type inference and default args # actor.receive() ?!
    */

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logit for handling the p2p request
      println("Handling P2P Request")
      42
    }
  }

  implicit class FromP2PResponse(request: P2PResposne) extends MessageMagnet[Int] {
    override def apply(): Int = {
      // logit for handling the p2p request
      println("Handling P2P Response")
      24
    }
  }

  receive(new P2PRequest) // implicit conversion here from P2PRequest to MessageMagnet
  receive(new P2PResposne)

  /**
    * Benefits of Magnet Pattern:
    *   - no more type erasure problems
    */

  implicit class FromResponseFuture(future: Future[P2PResposne]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  println(receive(Future( new P2PRequest)))
  println(receive(Future ( new P2PResposne)))

  // Lifting works with a small catch
  trait MathLib {
    def add1(x: Int) = x + 1
    def add1(str: String) = str.toInt + 1
    // and a bunch of overloads
  }

  // "Magnetize" the MathLib
  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet{
    override def apply(): Int = s.toInt + 1
  }

  val addFv = add1 _ // this can receive an int o a string

  println(addFv(1))
  println(addFv("1"))

  /**
    * Drawbacks of the magnet pattern
    *   1. Super Verbose
    *   2. Harder to read
    *   3. You can't name or place default arguments
    *   4. Call by name does not work correctly
    */

  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet.apply()

  implicit class StringHandle(str: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(str)
      println(str)
    }
  }


  def sideEffectMethod(): String = {
    println("Hello scala")
    "haha"
  }

  handle(sideEffectMethod())
  // Below Hello Scala will only be printed once!
  handle {
    println("Hello scala")
    "haha" // only this bit is magnetized or converted into new StringHandler("magnet")
  }


}
