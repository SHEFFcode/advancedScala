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

//  multipleProdConst(3, 3)

  /**
    * Excercises
    * 1) Here the notify and notifyAll act in a similar manner, think of a case where they differ
    * 2) Create Deadlock - one or more threads block each other and cannot continue
    * 3) Create a LiveLock - all threads yield execution to each other in such a way that no one can continue
    */

  // 1) Notify all
  def testNotifyAll(): Unit = {
    val bell = new Object // POJO
    (1 to 10).foreach( i => new Thread(() => {
      bell.synchronized {
        println(s"[Thread $i]: I am waiting for the bell to ring...")
        bell.wait()
        println(s"[Thread $i]: hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println(s"[Announcer]: Rock and roll!")
//      bell.synchronized(bell.notifyAll())
      bell.synchronized(bell.notify()) // this will keep the program running, but the sleeping threads will never wake up
    }).start()
  }

//  testNotifyAll()

  /**
    * Deadlock - Imagine a society where you greet each other by bowing, and u can only straighten out when the other person started to
    * This creates a situation where you will both stay bowed forever
    */

  case class Friend(name: String) {
    def bow(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: oh please $other, feel free to pass!")
        switchSide()
        Thread.sleep(1000) // to allow some time for my friend to pass
      }
    }
  }

  val sam = Friend("Same")
  val pierre = Friend("Pierre")

//  new Thread(() => sam.bow(pierre)).start() // this will case deadlock, both will stay bowed
//  new Thread(() => pierre.bow(sam)).start()

  /**
    * 3 Livelock - extremely polite society
    * You and your friend come in from different directions on the same road, that you will give way to your friend to cross
    */

  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()

}
