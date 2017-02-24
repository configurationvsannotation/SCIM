package models

import play.api.libs.json.Json

case class Error(detail:String, status:String)

object Error {
  implicit val formatter = Json.format[Error]
}
