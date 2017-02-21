package models

import play.api.libs.json.Json

case class Name (familyName:String, givenName:String)

object Name {
  implicit val formatter = Json.format[Name]
}
