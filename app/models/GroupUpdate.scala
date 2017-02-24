package models

import play.api.libs.json.Json

case class GroupUpdate(Operations:List[Operations])

object GroupUpdate{
  implicit val formatter = Json.format[GroupUpdate]
}


