package us.oyanglul.dhallbin
package resource

import cats.effect._
import org.http4s.client.Client

import scala.concurrent.ExecutionContext
import org.http4s.client.blaze._
import org.http4s.client.middleware.FollowRedirect

trait HasClient {
  val client: Client[IO]
}

object http {
  def mk(implicit ctx: ContextShift[IO]): Resource[IO, Client[IO]] =
    BlazeClientBuilder[IO](ExecutionContext.global).resource

  object middleware {
    def followRedirect(client: Client[IO])(implicit ctx: ContextShift[IO]) = FollowRedirect(3)(client)
  }
}
