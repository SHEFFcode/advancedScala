package playground

object ScalaPlayground  extends App {
  println("Hello world")
  object Solution {
    def multiply(num1: String, num2: String): String = {
      val pos: Array[Int] = new Array[Int](num1.length + num2.length)

      for {
        i <- Range(num1.length - 1, -1, -1)
        j <- Range(num2.length - 1, -1, -1)
      } yield {
        val mul = (num1(i) - '0') * (num2(j) - '0')
        val p1 = i + j
        val p2 = i + j + 1
        val sum = mul + pos(p2)
        pos(p1) += sum / 10
        pos(p2) = sum % 10
      }
      pos.mkString.replaceFirst("^0+(?!$)", "")
    }
  }

  val solution = Solution.multiply("123", "456")

}
