package us.oyanglul.dhallbin

import cats.effect._

trait AppResource
    extends HasConfig
    with resource.HasClient
    with resource.HasToggle
    with resource.HasTracer
    with resource.HasLogger

package object resource {
  def mk(implicit ctx: ContextShift[IO]): Resource[IO, Resource[IO, AppResource]] =
    for {
      cl <- http.mk
      cfg <- Resource.liftF(Config.all(cl))
    } yield Resource.make(IO {
      new AppResource {
        val config = cfg
        val client = cl
      }
    }) { res =>
      res.logEval
    }
}
