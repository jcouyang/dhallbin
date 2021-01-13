package us.oyanglul.dhallbin

import cats.syntax.all._

package object route {
  val all = lambda.post <+> normalize.post <+> config.get <+> gist.get
}
