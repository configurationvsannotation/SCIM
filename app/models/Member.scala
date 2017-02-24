package models

import play.api.libs.json.Json

case class Member(value:String, $ref:String, display:String)

object Member{
  val resource = "http://localhost:9000/scim/v2/Users/"
  implicit val formatter = Json.format[Member]
}
