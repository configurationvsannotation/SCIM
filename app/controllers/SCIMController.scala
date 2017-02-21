package controllers

import javax.inject._

import models.{Name, User, UserUpdate}
import play.api.libs.iteratee.Enumeratee
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import service.UserService


class SCIMController @Inject() (userService: UserService) extends Controller {

  def users(filter:Option[String], count:Option[String], startIndex:Option[String])= Action {
    // TODO: Retrieve paginated models.User Objects
    // TODO: Allow for an equals and startsWith filters on username
    Ok
  }

  def user(uid:String)= Action {
    userService.userById(uid) match {
      case Some(user) => Ok(Json.toJson(user))
      case _ => Ok("{}")
    }
 }

  def createUser()= Action { request =>
    val userUpdate:Option[UserUpdate] = userUpdateFromJson(request)
    Created(userService.createUser(userUpdate.get))
  }

  private def userUpdateFromJson(request: Request[AnyContent]) = {
    request.body.asJson match {
      case Some(json) => Json.fromJson[UserUpdate](json).asOpt
      case _ => None
    }
  }

  def updateUser(uid:String)= Action { request =>
    val maybeUser = userService.userById(uid)
    maybeUser match {
      case Some(user) =>
        userUpdateFromJson(request) match {
          case Some(userUpdate) =>
            userService.updateUser(User(uid, Name(userUpdate.firstName.getOrElse(user.name.givenName),
              userUpdate.lastName.getOrElse(user.name.familyName)),user.userName,
              userUpdate.active.getOrElse(user.active)))
        }
        Ok
      case _ => BadRequest
    }

  }

  def deleteUser(uid:String)= Action {
    userService.deleteUser(uid)
    Ok
  }

  def groups(count:Option[String], startIndex:Option[String])= Action {
    // TODO: Retrieve paginated Group Objects
    Ok
  }

  def group(groupId:String)= Action {
    // TODO: Retrieve a single Group Object by ID
    Ok
  }

  def patchGroup(groupId:String)= Action {
    // TODO: Patch a Group Object, modifying its members
    Ok
  }

}
