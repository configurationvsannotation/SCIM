package models

import play.api.libs.json.Json

case class Groups (groups:List[Group])

object Groups{
  implicit val formatter = Json.format[Groups]
}

