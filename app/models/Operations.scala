package models

import play.api.libs.json.Json

case class Operations (op:String,value:List[Member])

object Operations {
  implicit val formatter = Json.format[Operations]
}