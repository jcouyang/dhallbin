package us.oyanglul.dhallbin
package service

import AppDsl._
import org.dhallj.core.Expr
import org.dhallj.core.converters.JsonConverter
import org.dhallj.yaml.YamlConverter

object expr {
  def format(expr: Either[Throwable, Expr], f: Option[String]) = expr match {
    case Left(e) => UnprocessableEntity(e.toString)
    case Right(expr) =>
      Ok(f match {
        case Some("json") => JsonConverter.toCompactString(expr)
        case Some("yaml") => YamlConverter.toYamlString(expr)
        case Some("sha")  => expr.hash
        case _            => expr.toString
      })
  }
}
