package lectures.part3.concurrency

import java.util.concurrent.Executors

object Intro extends App {
  //JVM Threads
  /**
    * Normally in the JVM we have an interface called runnable, which has a method called run, which can no params
    */

  val runnable = new Runnable { // anonymous class
    override def run(): Unit = println("Running in parallel")
  }

  val aThread = new Thread(runnable)

  /**
    * Starting a thread will actually create a JVM thread, which runs on top of the OS thread
    */

  aThread.start() // starting a thread, this only gives the signal to the JVm
  runnable.run() // Does not do anything in parallel
  aThread.join() // block until the thread has finished running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
  val threadGoodBye = new Thread(() => (1 to 5).foreach(_ => println("Goodbye")))

  threadHello.start()
  threadGoodBye.start()
  /**
    * Different runs in a multithreaded environment produces different results
    */

  /**
    * Executors, threads are expensive to start and kill, so you want to reuse them
    */

  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("Something in the thread pool")) // I don't care about starting and stopping threads, which is cool

//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Done after 1 seconds")
//  })
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("Almost done")
//    Thread.sleep(1000)
//    println("Done after 2 second")
//  })

  //After I am done with all of my thread pools
  pool.shutdown() // no more actions can be submitted
//  pool.execute(() => println("Should not appear")) // throws exception in the calling thread

//  pool.shutdownNow() // interrupts the sleeping threads

//  println(pool.isShutdown) // true even if the actions submitted the the pool are still running

  def runInParallel: Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x)

  }

//  for (_ <- 1 to 100000) runInParallel

  // This is called a race condition because 2 threads are trying to set the same memory zone at the same time

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
//    println("I bought " + thing)
//    println("My account has " + account)
  }

  for (_ <- 1 to 1000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buy(account, "shoes", 3000))
    val thread2 = new Thread(() => buy(account, "iPhone12", 4000))

    thread1.start()
    thread2.start()
    Thread.sleep(10) // this sleeps the main thread
    if (account.amount != 43000) println("AHA! " + account.amount)
//    println("========================================") // line to diff between calls

    /**
      * thread1(shoes): 50000
      *   - account.amount = 50000 - 3000
      * thread2(iPhone): 50000
      *   - account.amount = 50000 - 4000, overwrites memory of account.amount, this is the final amount
      * We address this issue by using the following options:
      *   # 1 use synchronized(), which is a method on reference types
      *   # 2 use an annotation called @volatile
      *
      * Synchronized is used more, as you can put in a lot more expressions in that block that you pass in.
      */

    //Synchronized method
    def buySafe(account: BankAccount, thing: String, price: Int) = account.synchronized {
      account.amount -= price
      println("I bought " + thing)
      println("My account has " + account)
    } // no 2 threads can eval at the same time
  }

  /**
    * Excercises:
    * 1) Construct 50 "inception" threads (threads that construct other threads)
    *   - Each thread should console log "Hello from thread #3" in Reverse order
    */

  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
    if (i < maxThreads) {
      val newThread = new Thread(inceptionThreads(maxThreads, i + 1))
      newThread.start()
      newThread.join()
    }
    println(s"Hello from thread $i")
  })

  inceptionThreads(50).start()

  /**
    * 2) What is the biggest possible value for x - 100
    *     - What is the smallest possible value for x - 1, because if all of them read x = 0 at the same time, they will all say x is now 1
    */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())

  /**
    * 3) Sleep fallacy, what is the value of the message? Almost always: "Scala is awesome"
    *     - Is it guaranteed? NOT GUARANTEED
    *     - Why?
    * Main Thread:
    *   message = "Scala Sucks"
    *   awesomeThread.Start()
    *     sleep() - relieves execution, some other thread can be executed at discretion of the OS
    * AwesomeThread gets the CPU;
    *   sleep() also relieves execuion
    * OS gives the CPU to some important thread that takes the CPU more then 2 seconds
    * OS gives the CPU back to the main thread, not the AWESOME thread, which prints scala sucks
    * OS gives the CPU to awesome thread and message becomes scala is awesome, but by now is too late, since println already executed
    *
    * Basically sleep only guarantees that the thread has slept for AT LEAST, NOT EXACTLY that many ms
    */

  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks!"
  awesomeThread.start()
  Thread.sleep(2000)
  awesomeThread.join() // join here will guarantee that scala will be awesome
  println(message)

  /**
    * How do we fix this ^^
    * Synchronizing does not work here, only works for CONCURRENT modifications, here we no not have this problem
    * The only solution here is to call thread.join before doing the println on the message
    */



}
