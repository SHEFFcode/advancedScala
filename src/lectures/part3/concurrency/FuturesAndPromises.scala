package lectures.part3.concurrency

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

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

  Thread.sleep(2000) // again sleep here so that we get the futures to resolve
}

