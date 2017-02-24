package models

import play.api.libs.json.Json

case class Users (users:List[User])

object Users {
  implicit val format = Json.format[Users]
}
