package lectures.part3.concurrency

object ThreadCommunication extends App {
  /**
    * The producer / consumer problem
    * producer -> [ x ] sets a value inside the container
    * consumer [ x ] -> extract the value from the container
    * These two run in parallel
    */

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

  def naiveProducerConsumer(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer]: waiting...")

      while (container.isEmpty) {
        println("[Consumer: still waiting...]")
      }
      println("[Consumer]: I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer]: computing...")
      Thread.sleep(500)
      val value = 42
      println("[Producer]: I have produced, after long work the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

//  naiveProducerConsumer()
  /**
    * The above is ok, because we do make the consumer wait, but we do have this busy loop that does no work for us, wasting cycles
    * How do we fix this? wait and notify
    *
    * What is synchronized? It's a method that locks the object's monitor, any other object that tries to work on this object will block
    * Only AnyRef can have synchronized, primitive types do not have synchronized expressions
    */

  def smartProducerConsumer(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[Consumer]: waiting...")
      container.synchronized { // blocks the monitor for the container
        container.wait() // this suspends the container thread, until it is signaled that it may continue by some other thread
      }

      println("[Consumer]: I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[Producer]: Hard at work ...")
      Thread.sleep(2000)
      val value = 42
      container.synchronized { // also blocks that container's monitor
        println("[Producer]: I am producing " + value)
        container.set(value)
        container.notify() // releases the other thread, when the monitor unlocks
      }
    })

    consumer.start()
    producer.start()
  }

  smartProducerConsumer()

}
