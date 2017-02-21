package models

import play.api.libs.json.Json


case class User (id:String, name:Name, userName:String, active: Boolean)

object User{
  implicit val formatter = Json.format[User]
}