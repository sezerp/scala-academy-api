package com.adform.scalaacademy

abstract class Fail extends Exception

object Fail {
  case object InternalServerError        extends Fail
  case class NotFound(what: String)      extends Fail
  case class Conflict(msg: String)       extends Fail
  case class IncorrectInput(msg: String) extends Fail
  case class Unauthorized(msg: String)   extends Fail
  case object Forbidden                  extends Fail
}
