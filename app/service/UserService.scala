package service

import java.util.UUID
import javax.inject.Inject

import com.google.inject.ImplementedBy
import models._
import play.api.Logger
import play.api.db.Database

import scala.collection.mutable.ArrayBuffer

@ImplementedBy(classOf[MySqlUserService])
trait UserService {
  def userById(userId:String):Option[User]
  def createUser(userUpdate: User):Option[User]
  def userExists(userName:String):Boolean
  def deleteUser(userId:String):Boolean
  def updateUser(user:User):Boolean
  def searchUsers(filter:Option[String], count:Option[String], startIndex:Option[String]):Users

  def groupById(groupId:String):Option[Group]
  def membersByGroupId(groupId:String):List[Member]
  def searchGroups(count:Option[String], startIndex:Option[String]):Groups

}

class MySqlUserService @Inject() (db:Database) extends UserService{

  val logger = Logger.logger

  override def userById(userId: String): Option[User] = {
    println(s"userById:$userId")
    val userQuery = "select id_str, firstName, lastName, active, userName from users where id_str = ?"
    val query = prepareStatement(userQuery,userId).executeQuery()
    if(query.next()){
      Some(User(Some(query.getString("id_str")),Name(query.getString("lastName"),query.getString("firstName")),
        query.getString("userName")))
    }else{
      None
    }
  }

  override def userExists(userName:String)={
    val userQuery = "select count(*) as cnt from users where userName = ?"
    val query = prepareStatement(userQuery,userName).executeQuery()
    query.next()
    query.getInt("cnt") >0
  }

  override def groupById(groupId: String): Option[Group] = {
    val groupQuery = "select id_str, displayName from groups where id_str = ?"
    val query = prepareStatement(groupQuery,groupId).executeQuery()
    if(query.next()){
      Some(Group(groupId,query.getString("displayName"),membersByGroupId(groupId)))
    }else{
      None
    }
  }

  override def createUser(userUpdate: User): Option[User] = {
    val userId = UUID.randomUUID().toString
    executeUdate("insert into users(id_str,firstName,lastName,active,userName) values(?,?,?,?,?)",userId,
      userUpdate.name.givenName,
      userUpdate.name.familyName,
      true,
      userUpdate.userName) match {
        case true => Some(userById(userId).get)
        case false => None
    }

  }
  //family name is last name

  override def deleteUser(userId: String): Boolean = {
    executeUdate("delete from user_groups where user_id in (select user_id from users where id_str = ?)",userId)
    executeUdate("delete from users where id_str = ?",userId)
  }

  override def updateUser(user:User):Boolean = {
    executeUdate("update users set firstName = ?, lastName = ?, active = ? where id_str = ?",user.name.givenName,
      user.name.familyName,true,user.id.get)
  }


  override def searchUsers(filter: Option[String], count: Option[String], startIndex: Option[String]): Users = {
    val searchUsersQuery =
      "select id_str, firstName, lastName, active, userName from users where userName like ? order by firstName limit ? offset ?"
    val query = prepareStatement(searchUsersQuery,s"%${filter.getOrElse("")}%",
      count.getOrElse("5").toInt,startIndex.getOrElse("0").toInt).executeQuery();
    val users = ArrayBuffer[User]()
    while(query.next()) users += User(Some(query.getString("id_str")),Name(query.getString("firstName"),
      query.getString("lastName")),query.getString("userName"))
    Users(users.toList)
  }

  override def membersByGroupId(groupId: String): List[Member] = {
    val query = prepareStatement(
      "select u.id_str, firstName, lastName  from users u, user_groups  ug, groups g " +
        "where ug.user_id = u.id  and ug.group_id = g.id and g.id_str = ? order by lastName, firstName", groupId).executeQuery();
    val users = ArrayBuffer[Member]()
    while(query.next()) {
      val id = query.getString("id_str")
      users += Member(id,Member.resource + id,
        query.getString("firstName") + " " + query.getString("lastName"))
    }
    users.toList
  }
  override def searchGroups(count: Option[String], startIndex: Option[String]): Groups = {
    val query = prepareStatement("select id_str from groups limit ? offset ?",count.getOrElse("5").toInt,
      startIndex.getOrElse("0").toInt).executeQuery();
    val groups = ArrayBuffer[Group]()
    while(query.next()) groups += groupById(query.getString("id_str")).get
    Groups(groups.toList)
  }

  private def executeUdate(s:String, args:Any*):Boolean = {
    prepareStatement(s, args:_*).executeUpdate() > 0
  }

  private def prepareStatement(sql:String, args:Any*)= {
    val preparedStatement = db.getConnection().prepareStatement(sql)
    var psIndex = 0

    args.foreach{ arg =>
      psIndex += 1
      arg match {
        case a:String => preparedStatement.setString(psIndex, a)
        case b:Boolean => preparedStatement.setBoolean(psIndex,b)
        case c=> {
          throw new RuntimeException(s"invalid paramater of class ${c.getClass} setting argument $psIndex for $sql ")
        }
      }
    }
    preparedStatement.closeOnCompletion()
    preparedStatement
  }


}
