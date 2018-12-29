package lectures.part3.concurrency

import scala.collection.mutable
import scala.util.Random

object ProducerConsumerTwo extends App {
  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0
    def set(newValue: Int): Unit = value = newValue
    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  /**
    * Producer -> [ ? ? ? ] -> Consumer
    * producer with three values, and the consumer extracts a new value inside this buffer
    * Both the producer and the consumer may block each other
    * Imagine if the buffer is full, the producer must block until the consumer has consumed a value
    * BUT if there are no values to extract, we will need the consumer to block util the producer has had time to produce
    */

  def prodConsLargeBuffer() = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]()
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[Consumer]: buffer is empty, waiting")
            buffer.wait() // suspend the thread here
          }
          // If I am here, the buffer must not be empty
          val x = buffer.dequeue()
          println("[Consumer]: I consumed the value " + x)

          buffer.notify() // we got a value, so we can get the producer (or another consumer) to start rolling again
        }
        Thread.sleep(random.nextInt(500)) // 500 ms max
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[Producer]: buffer is full, so waiting...")
            buffer.wait() // wait for the consumer to wake me up
          }

          // If I am here, there must be at least one empty space in the buffer for me to produce the value
          println("[Producer] producing " + i)
          buffer.enqueue(i)

          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

  prodConsLargeBuffer()
}
