import zio.*
import zio.stream.*

object Day4 extends ZIOAppDefault {

  val source: String => ZStream[Any, Throwable, String] =
    fileName =>
      ZStream
        .fromFileName(fileName)
        .via(ZPipeline.utfDecode >>> ZPipeline.splitLines)

  trait ElfJanitor {
    val assignedTo: Range
  }

  object ElfJanitor {

    def apply(line: String): (ElfJanitor, ElfJanitor) = {
      val elves: Array[ElfJanitor] = line
        .split(",")
        .map { rngStr =>
          val bounds = rngStr.split("-").map(_.toInt).take(2)
          new ElfJanitor {
            override val assignedTo: Range = bounds.head to bounds.last
          }
        }
        .take(2)
      (elves.head, elves.last)
    }

    extension(workers: (ElfJanitor, ElfJanitor)) {
      // Indicates if an Elf is redundant, and the index of which one is, if any.
      def redundant: Option[Int] =
        (workers._1.assignedTo.toSet, workers._2.assignedTo.toSet) match {
          case (a, b) if a.subsetOf(b) => Some(1)
          case (a, b) if b.subsetOf(a) => Some(2)
          case _                       => None
        }
      // Indicates if there is any overlap to assigned work
      def overlap: Boolean =
        (workers._1.assignedTo.toSet, workers._2.assignedTo.toSet) match {
          case (a, b) if a.union(b).size < a.size + b.size => true
          case _                                           => false
        }
    }

  }

  val data = "day-4-1.data"
  override def run: ZIO[Any, Any, Any] = for {
    _ <- source(data)
      .map(ElfJanitor.apply)
      .filter(_.redundant.isDefined)
      .run(ZSink.count)
      .debug("Answer Pt.1")
    _ <- source(data)
      .map(ElfJanitor.apply)
      .filter(_.overlap)
      .run(ZSink.count)
      .debug("Answer Pt.2")
  } yield ExitCode.success

}
