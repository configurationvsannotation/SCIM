package models

import play.api.libs.json.Json

case class GroupUpdate(groupId:String,adds:List[String],removes:List[String])

object GroupUpdate{
  implicit val formatter = Json.format[GroupUpdate]
}
