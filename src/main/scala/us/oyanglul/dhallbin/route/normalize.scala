package us.oyanglul.dhallbin
package route

import AppDsl._
import cats.implicits._
import com.twitter.logging.Logger
import resource.logger._
import org.dhallj.syntax._
import org.dhallj.core.converters.JsonConverter
import org.dhallj.yaml.YamlConverter
import org.dhallj.imports.syntax._
import cats.effect.IO
import cats.data.Kleisli

object normalize {
  implicit val log = Logger.get()
  object FormatMatcher extends OptionalQueryParamDecoderMatcher[String]("format")

  val post = AppRoute {
    case req @ POST -> Root / "normalize" :? FormatMatcher(format) =>
      for {
        _ <- log.infoF("normalizing expression")
        client <- Kleisli.ask[IO, AppResource].map(_.client)
        payload <- NT.IOtoApp(req.as[String])
        resolved <- {
          implicit val c = client
          NT.IOtoApp(IO.fromEither(payload.parseExpr)
                  .flatMap(_.resolveImports[IO])).attempt
        }
        nomalized =
        resolved
            .map(_.normalize)
            .map { expr =>
              format match {
                case Some("json") => JsonConverter.toCompactString(expr)
                case Some("yaml") => YamlConverter.toYamlString(expr)
                case _            => expr.toString
              }
            }
        resp <- {
          nomalized match {
          case Left(e)  => UnprocessableEntity(e.toString)
          case Right(b) => Ok(b)
          }

        }
      } yield resp
  }
}