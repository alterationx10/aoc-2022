import zio.*
import zio.stream.*

import scala.language.postfixOps
import scala.reflect.ClassTag

object DoomsDay extends ZIOAppDefault {

  val simpleMatrix: Array[Array[String]] = Array(
    Array("🍒", "🍒", "🍒"),
    Array("🍋", "🍋", "🍋"),
    Array("🫐", "🫐", "🫐")
  )

  /** Given [[simpleMatrix]], will return: Transpose: 🍒,🍋,🫐 🍒,🍋,🫐 🍒,🍋,🫐
    *
    * @param m
    * @tparam A
    * @return
    */
  private def transpose[A: ClassTag](m: Array[Array[A]]): Array[Array[A]] = {
    (0 until m.head.size toArray).map { c =>
      (0 until m.size toArray) map { r =>
        m(r)(c)
      }
    }
  }

  /** Given [[simpleMatrix]], will return: Reverse Rows: 🍒,🍒,🍒 🍋,🍋,🍋
    * 🫐,🫐,🫐
    *
    * @param m
    * @tparam A
    * @return
    */
  private def reverseRows[A: ClassTag](m: Array[Array[A]]): Array[Array[A]] =
    m.map(_.reverse)

  /** Given [[simpleMatrix]], will return: Reverse Cols.: 🫐,🫐,🫐 🍋,🍋,🍋
    * 🍒,🍒,🍒
    *
    * @param m
    * @tparam A
    * @return
    */
  private def reverseCols[A: ClassTag](m: Array[Array[A]]): Array[Array[A]] = {
    val rows = m.size - 1
    val cols = m.head.size - 1
    (0 to rows toArray).map { r =>
      (0 to cols toArray).map { c =>
        m(rows - r)(c)
      }
    }
  }

  /** Given [[simpleMatrix]], will return: Rotate Clockwise: 🫐,🍋,🍒 🫐,🍋,🍒
    * 🫐,🍋,🍒
    *
    * @param arr
    * @param clockWise
    * @tparam A
    * @return
    */
  private def rotateArray[A: ClassTag](
      arr: Array[Array[A]],
      clockWise: Boolean = true
  ): Array[Array[A]] = {
    if (clockWise) {
      reverseRows(transpose(arr))
    } else {
      reverseCols(transpose(arr))
    }
  }

  val source: String => ZStream[Any, Throwable, String] =
    fileName =>
      ZStream
        .fromFileName(fileName)
        .via(ZPipeline.utfDecode >>> ZPipeline.splitLines)
        .filter(_.nonEmpty)

  /** Our *clean* data should have n m-x-m matrices. That means each row has m
    * elements, and n*m rows.
    *
    * Given: 🍒 🍒 🍒 🍋 🍋 🍋 🫐 🫐 🫐 🍒 🍒 🍒 🍋 🍋 🍋 🫐 🫐 🫐 🍒 🍒 🍒 🍋
    * 🍋 🍋 🫐 🫐 🫐
    *
    * Will output: 🫐 🍋 🍒 🫐 🍋 🍒 🫐 🍋 🍒 🫐 🍋 🍒 🫐 🍋 🍒 🫐 🍋 🍒 🫐 🍋
    * 🍒 🫐 🍋 🍒 🫐 🍋 🍒
    *
    * @return
    */

  override def run: ZIO[Any, Any, Any] =
    for {
      m <- source("doomsday.data")
             .take(1)
             .map(_.split(" ").length)
             .run(ZSink.sum)
             .debug("m")
      _ <- source("doomsday.data")
             .map(_.split(" "))
             .grouped(m)
             .map(chunk => rotateArray(chunk.toArray))
             .tap(arr =>
               Console.printLine(arr.map(_.mkString(" ")).mkString("\n"))
             ) // Just for pretty-printing
             .runDrain
    } yield ExitCode.success
}
