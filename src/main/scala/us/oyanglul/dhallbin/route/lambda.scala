package us.oyanglul.dhallbin
package route

import AppDsl._
import cats.implicits._
import com.twitter.logging.Logger
import resource.logger._
import org.dhallj.syntax._
import org.dhallj.core.converters.JsonConverter
import org.dhallj.yaml.YamlConverter
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
        resp <- (for (f <- fn; a <- arg; _ <- f(a).typeCheck) yield f(a).normalize) match {
          case Left(e) => UnprocessableEntity(e.toString)
          case Right(expr) =>
            Ok(format match {
              case Some("json") => JsonConverter.toCompactString(expr)
              case Some("yaml") => YamlConverter.toYamlString(expr)
              case Some("sha")  => expr.hash
              case _            => expr.toString
            })
        }
      } yield resp
  }
}
