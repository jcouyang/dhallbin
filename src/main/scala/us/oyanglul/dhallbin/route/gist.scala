package us.oyanglul.dhallbin
package route

import AppDsl._
import cats.implicits._
import com.twitter.logging.Logger
import resource.logger._
import org.dhallj.syntax._
import org.dhallj.imports.syntax._
import cats.effect.IO
import cats.data.Kleisli

object gist {
  implicit val log = Logger.get()
  object FormatMatcher extends OptionalQueryParamDecoderMatcher[String]("format")
    object LocationMatcher extends QueryParamDecoderMatcher[String]("location")

  val get = AppRoute {
    case GET -> Root / "gist" :? FormatMatcher(format) :? LocationMatcher(location) =>
      for {
        _ <- log.infoF("normalizing expression")
        client <- Kleisli.ask[IO, AppResource].map(_.client)
        resolved <- NT.IOtoApp {
          implicit val c =client
          c.expect[String](location).flatMap{ payload =>
            IO.fromEither(payload.parseExpr)
          }.flatMap(_.resolveImports[IO]).attempt
        }
        resp <- service.expr.format(resolved.map(_.normalize), format)
      } yield resp
  }
}
