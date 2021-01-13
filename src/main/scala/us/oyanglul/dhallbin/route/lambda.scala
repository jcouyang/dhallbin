package us.oyanglul.dhallbin
package route

import AppDsl._
import cats.implicits._
import com.twitter.logging.Logger
import resource.logger._
import org.dhallj.syntax._
import io.circe.Json
import org.http4s.circe.CirceEntityDecoder._

object lambda {
  implicit val log = Logger.get()
  object FormatMatcher extends OptionalQueryParamDecoderMatcher[String]("format")

  val post = AppRoute {
    case req @ POST -> Root / "lambda" :? FormatMatcher(format) =>
      for {
        _ <- log.infoF("executing expression")
        payload <- NT.IOtoApp(req.as[Json])
        arg = payload.hcursor.get[String]("arg").flatMap(_.parseExpr).map(_.normalize)
        fn = payload.hcursor.get[String]("lambda").flatMap(_.parseExpr).map(_.normalize)
        resp <- service.expr.format(for (f <- fn; a <- arg; _ <- f(a).typeCheck) yield f(a).normalize, format)
      } yield resp
  }
}
