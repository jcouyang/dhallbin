package us.oyanglul.dhallbin

import org.dhallj.syntax._
import org.dhallj.codec.syntax._
import us.oyanglul.dhall.generic._
import org.dhallj.imports.syntax._
import cats.effect.IO
import org.http4s.client.Client
import org.http4s.Uri

sealed trait Env

case object Local extends Env
case object PreProd extends Env
case object Prod extends Env

case class Application(env: Env)
case class Config(app: Application, sha: String)
object Config {
  def all(implicit client: Client[IO]) = {
      IO.fromEither("classpath:/application.dhall".parseExpr)
        .flatMap(_.resolveImports[IO])
        .flatMap(expr => IO.fromEither(expr.normalize.as[Application]).map(Config(_, expr.normalize().hash())))
  }
  implicit val dhallDecodeUri: org.dhallj.codec.Decoder[Uri] =
    org.dhallj.codec.Decoder.decodeString.map(Uri.unsafeFromString(_))
}

trait HasConfig {
  val config: Config
}
