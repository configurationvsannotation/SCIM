package models

import play.api.libs.json.Json


case class User (id:Option[String], name:Name, userName:String)

object User{
  val resource = "http://localhost:9000/scim/v2/Users/"
  implicit val formatter = Json.format[User]
}