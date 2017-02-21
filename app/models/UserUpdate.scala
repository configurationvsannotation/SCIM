package models

import play.api.libs.json.Json

case class UserUpdate (firstName:Option[String], lastName:Option[String], active:Option[Boolean], userName:Option[String])

object UserUpdate{
  implicit val formatter = Json.format[UserUpdate]
}