package lectures.part3.concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.{ForkJoinTaskSupport, Task, TaskSupport}
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {
   // 1 parallel collections
  val parList = List(1, 2, 3).par // this now becomes thread safe
  val aParVector = ParVector[Int](1, 2, 3)

  /**
    * Parallel collections include:
    *   - Seq
    *   - Vector
    *   - Array
    *   - Map # HashMap, TrieMaps
    *   - Set # HashSet, TrieSets
    *   -
    */

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000000).toList
  val serialTime = measure(list.map(_ + 1))
  val parallelTime = measure(list.par.map(_ + 1))

  println(s"serialTime is $serialTime")
  println(s"parallelTime is $parallelTime")

  /**
    * Performance benefits of parallel collections are only good for very large collections, because of the cost of spinning up a thread
    * Parallel collections work on the map-reduce model:
    *   - Split the elements into chunks, which will be processed independently in a single thread
    *   - Operation takes place on each of the chunks in a separate thread
    *   - Results are recombined by the combiner
    * Note:
    *   - map, flatMap, filter and foreach are safe
    *   - reduce and fold are NOT
    */

  println(List(1, 2, 3).reduce(_ - _)) // -4, which is correct (1 - 2 - 3
  println(List(1, 2, 3).par.reduce(_ -_)) // 2 because we cannot guarantee the order of ops

  // Synchronization
  var sum = 0
  List(1, 2, 3).par.foreach(sum += _) // we should see 6, but its NOT guaranteed, sum could be accessed by 2 threads at same time

  // configuration
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2)) // number of threads that will manage the parallel vector
  /**
    * Alternative configurations:
    *   - ThreadPoolTaskSupport - DEPRECATED
    *   - ExecutionContextTaskSupport
    *   - You can even roll your own taskSupport
    */

  aParVector.tasksupport = new TaskSupport {
    override val environment: AnyRef = _

    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    override def parallelismLevel: Int = ???
  }

  // Atomic ops and references, atomic as it either runs fully or not at all
  val atomic = new AtomicReference[Int](2)
  val currentValue = atomic.get() // thread safe, no other thread can read or write to this atomic reference
  atomic.set(4) // thread safe write
  atomic.getAndSet(5) // threadsafe combo!
  atomic.compareAndSet(38, 56) // this does shallow or reference equality
  atomic.updateAndGet(_ + 1) // thread safe function run
  atomic.getAndUpdate(_ + 1) // get the old value, and then run
  atomic.accumulateAndGet(12, _ + _) // thread safe accumulation
  atomic.getAndAccumulate(12, _ + _) // thread safe accumulation but get first


}
