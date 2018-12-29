package lectures.part3.concurrency

import scala.concurrent.Future
import scala.util.{Failure, Success}

//important for futures
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesAndPromises extends App {
  def calculateMeaningOflife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOflife // this calculates the meaning of life on another thread
  } //(global) which is passed in by the compiler

  println(aFuture.value) // Option[Try[Int]], which is None

  println("Waiting on the future")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"Meaning of life is $meaningOfLife")
    case Failure(exception) => println(s"I failed with $exception")
  } // SOME THREAD executes this partial function, we don't know which one, we do not get the result of evaluation, its always unit

  Thread.sleep(3000) // we want the main thread not to exit and wait for side thread to complete


}

