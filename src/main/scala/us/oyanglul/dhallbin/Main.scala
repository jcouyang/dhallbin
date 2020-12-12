package us.oyanglul.dhallbin

import scala.concurrent.ExecutionContext
import cats.effect._
import com.twitter.finagle.Http
import com.twitter.util.Await
import com.twitter.server.TwitterServer
import org.http4s.finagle.Finagle
import org.http4s.implicits._
import org.http4s.HttpRoutes
import cats.data.OptionT

object Main extends TwitterServer {
  implicit val ctx: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  val port = flag("port", ":8080", "Service Port Number")
  def main() =
    resource.mk
      .use { (deps: Resource[IO, AppResource]) =>
        val service: HttpRoutes[IO] = route.all.flatMapF(resp => OptionT.liftF(deps.use(r => resp.run(r))))
        val server = Http.server
          .withHttp2
          .withLabel("dhallbin")
          .serve(port(), Finagle.mkService[IO](service.orNotFound))
        logger.info(s"Server Started on ${port()}")
        onExit {
          server.close()
          ()
        }
        IO(Await.ready(server))
      }
      .unsafeRunSync()
}
