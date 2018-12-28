package playground

object ScalaPlayground  extends App {
  println("Hello world")
  object Solution {
    def findMissingRanges(nums: Array[Int], lower: Int, upper: Int): List[String] = {
      val missingRanges = new StringBuilder
      if (nums.isEmpty) {
        finalOperation(lower, upper, missingRanges)
        return if (missingRanges.nonEmpty) missingRanges.toString.split(',').toList else List()
      }

      def finalOperation(lower: Int, upper: Int, sb: StringBuilder) = {
        if (lower < upper) sb ++= s"$lower->$upper"
        if (lower == upper) sb ++= s"$lower"
      }

      def recurse(nums: Array[Int], lower: Int, upper: Int, sb: StringBuilder, index: Int): StringBuilder = {
        val num = nums(index)
        nums(index) match {
          case n if lower < n - 1 => {
            sb ++= s"$lower->${num - 1},"
            if (index + 1 == nums.length - 1)  {
              finalOperation(lower, upper, sb)
              return sb
            }
            recurse(nums, num + 1, upper, sb, index + 1)

          }
          case n if lower == n - 1 => {
            if (index + 1 == nums.length - 1)  {
              sb ++= s"$lower,"
              finalOperation(lower, upper, sb)
              return sb
            }
            recurse(nums, num + 1, upper, sb, index + 1)

          }
          case n if n == Integer.MAX_VALUE => return sb
        }
      }

      recurse(nums, lower, upper, missingRanges, 0)

      if (missingRanges.nonEmpty) missingRanges.toString.split(',').toList else List()
    }
  }

}
