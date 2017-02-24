package models

import play.api.libs.json.Json

case class Group (id:String,displayName:String,members:List[Member])

object Group{
 val resource = "http://localhost:9000//scim/v2/Groups/"
 implicit val formatter = Json.format[Group]
}

