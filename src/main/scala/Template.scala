import zio.*
import zio.stream.*

object Template extends ZIOAppDefault {

  val source: String => ZStream[Any, Throwable, String] =
    fileName =>
      ZStream.fromFileName(fileName).via(ZPipeline.utfDecode >>> ZPipeline.splitLines)


  val data = ""
  override def run: ZIO[Any, Any, Any] = for {
    _ <- source(data).runDrain
  } yield ExitCode.success

}
