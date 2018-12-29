package lectures.part3.concurrency

import scala.collection.mutable
import scala.util.Random

object ProducerConsumerThree extends App {
  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          /**
            * Imagine the producer produced a value and 2 consumers are waiting.
            *   - Producer will call notify() and one of the consumers have been notified (notifies on buffer)
            *   - Now that the consumer has consumed, it will call its notify, which will notify the other consumer
            */

          while (buffer.isEmpty) { // we change to while here, because we may have been woken up, but no value in the buffer
            println(s"[Consumer $id]: buffer is empty, waiting")
            buffer.wait() // suspend the thread here
          }
          // If I am here, the buffer must not be empty
          val x = buffer.dequeue()
          println(s"[Consumer $id]: I consumed the value " + x)

          buffer.notify() // here we are asking SOMEBODY, not sure whom to wake up
        }
        Thread.sleep(random.nextInt(500)) // 500 ms max
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) { // again while here because we want to keep checking that the buffer is not full
            println(s"[Producer $id]: buffer is full, so waiting...")
            buffer.wait() // wait for the consumer to wake me up
          }

          // If I am here, there must be at least one empty space in the buffer for me to produce the value
          println(s"[Producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multipleProdConst(nConsumers: Int, nProducers: Int) = {
    val buffer = new mutable.Queue[Int]()
    val capacity = 3

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

  multipleProdConst(3, 3)
}
