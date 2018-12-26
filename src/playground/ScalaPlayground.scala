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

object Solution {
  def exist(board: Array[Array[Char]], word: String): Boolean = {
    for {
      i <- 0 to board.length
      j <- 0 to board(i).length
    } yield {
      def recurse(i: Int, j: Int, board: Array[Array[Char]], word: String, index: Int): Option[Boolean] = {
        if (board(i)(j) == word(index)) {
          if (index == word.length) return Option(true)
          val temp = board(j)(j)
          board(i)(j) = '.'
          val newIndex = index + 1
          val result = {
              recurse(i + 1, j, board, word, newIndex)
                .orElse(recurse(i - 1, j, board, word, newIndex)
                  .orElse(recurse(i, j + 1, board, word, newIndex)
                    .orElse(recurse(i, j - 1, board, word, newIndex).orElse(Option(false))
          }
          board(i)(j) = temp
          result
        }
        else Option(false)
      }
      recurse(i, j, board, word, 0)
    }
  }
}
