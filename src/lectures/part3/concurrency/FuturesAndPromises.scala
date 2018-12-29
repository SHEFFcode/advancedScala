package lectures.part3.concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._

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


  /**
    * You have been asked to design a mini social network
    */

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    //database
    val names: Map[String, String] = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy",
    )

    val friends: Map[String, String] = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    //API
    def fetchProfile(id: String): Future[Profile] = Future {
      // Simulates fetching from the DB
      Thread.sleep(random.nextInt(300)) // long computation
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }
  //client: mark to poke bill
  val mark: Future[Profile] = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      val bill: Future[Profile] = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile) // this is the only useful part of the code, is too deeply nested
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(e) => e.printStackTrace()
  }

  //Functional composition of futures
  //map, flatMap, filter

  // Map transforms a future into a different future
  val nameOnTheWall: Future[String] = mark.map(profile => profile.name) // from a future of profile we go to a future of string
  val marksBestFriend: Future[Profile] = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile)) // Another future
  val marksBestFriendRestricted: Future[Profile] = marksBestFriend.filter(profile => profile.name.startsWith("B"))

  // This means that we can write for comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill) // this is way cleaner

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unkown id").recover {
    case e: Throwable => Profile("fb.id.0-dummy", "Forever alone")
  }

  val aFetchProfileNoMatterWhat = SocialNetwork.fetchProfile("unkown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unkown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))


  /**
    * Small banking app here
    */

  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "SHEFFbank"

    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(200)
      User(name)
    }

    def createTransaction(user: User, merchant: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchant, amount, "Success")
    }

    def purchase(userName: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from the db
      //create a transaction
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(userName)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicit conversions -> pimp my library
    }
  }

  println(BankingApp.purchase("Jeremy", "iPhone 12", "SHEFFstore", 3000)) // this will block, no need for sleeping main thread

//  Thread.sleep(2000) // again sleep here so that we get the futures to resolve

  /**
    * Scala promises
    */

  val promise = Promise[Int]() // Controller over a future
  val future = promise.future

  //Thread # 1 - consumer
  future.onComplete {
    case Success(someResult) => println(s"[Consumer]: I have received $someResult")
  }

  // Thread #2 - producer
  val producer = new Thread(() => {
    println(s"[Producer]: crunching numbers...")
    Thread.sleep(500)
    // fullfilling the promise
    promise.success(42) // manipulates the internal future to complete with value 42
    println("[Producer]: done")
  })

  producer.start()

  Thread.sleep(1000)

  /**
    * Excercises:
    * 1) fulfill a future immediately with a value
    * 2) Run a function inSequence(fa, fb) => runs fb AFTER fa has been completed
    * 3) first(fa, fb) => Either a or b depending on which finished first
    * 4) last(fa, fb) => Either a or b depending on which is finished last
    * 5) retryUntil => Future[T], condition: T => Boolean , so keep trying until the action succeeds
    */

  // 1
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  // 2
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = {
    first.flatMap(_ => second)
  }

  // 3
  def first[A, B](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]

    def tryComplete[A](promise: Promise[A], result: Try[A]) = result match {
      case Success(r) => try {
        promise.success(r)
      } catch {
        case _ =>
      }
    }

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  // 4 last of the two features
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    // 1 promise that both futures will try to complete
    // 2 promise which LAST future will complete

    val bothPromise = Promise[A]
    val lastPromise = Promise[A]
    val checkAndComplete = (result: Try[A]) => if (!bothPromise.tryComplete(result)) lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future {
    Thread.sleep(200)
    45
  }

  first(fast, slow).foreach(println)
  last(fast, slow).foreach(println)

  // retry untill
  def retryUntil[A](action: () => Future[A], predicate: A => Boolean): Future[A] = {
    action().filter(predicate).recoverWith {
      case _ => retryUntil(action, predicate) // we will keep retrying until we succeed
    }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println(s"generated $nextValue")
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(println)


  Thread.sleep(10000)
}

