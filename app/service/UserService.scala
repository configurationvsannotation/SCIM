package service

import java.sql.PreparedStatement
import java.util.UUID
import javax.inject.Inject

import com.google.inject.ImplementedBy
import models.{Name, User, UserUpdate}
import play.api.db.Database
import play.api.Logger

@ImplementedBy(classOf[MySqlUserService])
trait UserService {
  def userById(userId:String):Option[User]
  def createUser(userUpdate: UserUpdate):String
  def deleteUser(userId:String):Boolean
  def updateUser(user:User):Boolean
  def search(filter:Option[String], count:Option[String], startIndex:Option[String]):List[User]

}

class MySqlUserService @Inject() (db:Database) extends UserService{

  override def userById(userId: String): Option[User] = {
    val userQuery = "select id_str, firstName, lastName, active, userName from users where id_str = ?"
    val preparedStatement: PreparedStatement = db.getConnection().prepareStatement(userQuery)
    preparedStatement.closeOnCompletion();
    preparedStatement.setString(1,userId)
    val query = preparedStatement.executeQuery()
    if(query.next()){
      Some(User(query.getString("id_str"),Name(query.getString("firstName"),query.getString("lastName")),query.getString("userName"), query.getBoolean("active")))
    }else{
      None
    }
  }

  override def createUser(userUpdate: UserUpdate): String = {
    val userId = UUID.randomUUID().toString
    executeUdate("insert into users(id_str,firstName,lastName,active,userName) values(?,?,?,?,?)",userId,
      userUpdate.firstName.get,
      userUpdate.lastName.get,
      userUpdate.active.get,
      userUpdate.userName.get)
    userId
  }

  override def deleteUser(userId: String): Boolean = {
    executeUdate("delete from users where id_str = ?",userId)
  }

  override def updateUser(user:User):Boolean = {
    executeUdate("update users set firstName = ?, lastName = ?, active = ? where id_str = ?",user.name.givenName,
      user.name.familyName,user.active,user.id)
  }


  override def search(filter: Option[String], count: Option[String], startIndex: Option[String]): List[User] = {
    val preparedStatement: PreparedStatement = db.getConnection().prepareStatement(
      "select id_str, firstName, lastName, active from users where where ")
    preparedStatement.closeOnCompletion();
    //preparedStatement.setString(1,user.name.givenName)
    List()
  }

  private def executeUdate(s:String, args:Any*):Boolean = {
    val preparedStatement: PreparedStatement = db.getConnection().prepareStatement(s)
    preparedStatement.closeOnCompletion();
    val as = args.toArray
    for(i <- 0 until as.length){
      as(i) match {
        case a:String => preparedStatement.setString((i + 1), a)
        case b:Boolean => preparedStatement.setBoolean(i + 1,b)
        case _ => throw new RuntimeException(s"invalid paramater of class ${as(i).getClass}")
      }

    }
    preparedStatement.execute()
  }
}
