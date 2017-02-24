package controllers

import javax.inject._

import models._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import service.UserService

import scala.collection.immutable.Seq
import play.api.Logger


class SCIMController @Inject() (userService: UserService) extends Controller {

  val logger = Logger.logger

  def users(filter:Option[String], count:Option[String], startIndex:Option[String])= Action {
    // TODO: Retrieve paginated models.User Objects
    // TODO: Allow for an equals and startsWith filters on username
    Ok(Json.toJson(userService.searchUsers(filter,count,startIndex)))
  }

  def user(uid:String)= Action {
    // TODO: Retrieve a single User Object by ID
    userService.userById(uid) match {
      case Some(user) => Ok(Json.toJson(user)).withHeaders(userHeaders(user):_*)
      case _ => NotFound(Json.toJson(Error(s"Resource $uid not found","404")))
    }
  }

  def createUser()= Action { implicit request =>
    // TODO: Create a User Object with firstname and lastname metadata
    userUpdateFromJson(request) match {
      case Some(usr) => {
        if (userService.userExists(usr.userName)) {
          Conflict
        } else {
          val use = userService.createUser(usr).get
          Created(Json.toJson(use)).withHeaders(userHeaders(use): _*)

        }
      }
      case None => error(s"Invalid Json body-couldn't actually get here","400")
    }
  }

  def updateUser(uid:String)= Action { implicit request =>
    // TODO: Update a User Object's firstname, lastname, and active status
    logger.debug("update user with {}",uid)
    userService.userById(uid) match{
      case None =>  NotFound(Json.toJson(Error(s"Resource $uid not found","404")))
      case Some(dbUser) =>
        logger.debug("preparing to update user {}",uid)
        userUpdateFromJson(request) match {
          case Some(user) =>{
            if (user.id.isDefined && user.id != dbUser.id) {
              error(s"Attribute 'id' is readonly", "400")
            } else if (user.userName != dbUser.userName) {
              error(s"Attribute 'userName' is readonly", "400")
            } else {
              userService.updateUser(user) match {
                case true => Ok(Json.toJson(userService.userById(uid).get))
                case false => error(s"Resource update failed", "400")
              }

            }
        }
          case None => error(s"Invalid input in body","400")
        }
    }


  }

  def deleteUser(uid:String)= Action {
    // TODO: Delete a User Object by ID
    userService.deleteUser(uid) match {
      case true => NoContent
      case false => NotFound(Json.toJson(Error(s"Resource $uid not found","404")))
    }
  }

  def groups(count:Option[String], startIndex:Option[String])= Action {
    // TODO: Retrieve paginated Group Objects
    Ok(Json.toJson(userService.searchGroups(count,startIndex)))
  }

  def group(groupId:String)= Action { implicit request =>
    // TODO: Retrieve a single Group Object by ID
    userService.groupById(groupId) match {
      case Some(group) => Ok(Json.toJson(group)).withHeaders(groupHeaders(group):_*)
      case _ => NotFound
    }
  }

  def patchGroup(groupId:String)= Action { implicit request =>
    // TODO: Patch a Group Object, modifying its members
    request.body.asJson match{
      case Some(json) => {
        Json.fromJson[GroupUpdate](json).asOpt match {
          case Some(groupUpdate) => Ok(Json.toJson(userService.patchGroup(groupId,groupUpdate)))
          case None => error(s"Bad patch request for group: $groupId","400")
        }
      }
      case None => error(s"Bad patch request for group: $groupId","400")
    }
  }
  private def error(message:String, status:String)(implicit request:Request[AnyContent]) ={
    BadRequest(Json.toJson(Error(message,status)))
  }

  private def userUpdateFromJson(request: Request[AnyContent]) = {
    request.body.asJson match {
      case Some(json) => Json.fromJson[User](json).asOpt
      case _ => None
    }
  }
  private def groupHeaders(group:Group): Seq[(String, String)] = {
    List("Content-Type" -> "application/scim+json","Location" -> (Group.resource + group.id))
  }
  private def userHeaders(user:User): Seq[(String, String)] = {
    List("Content-Type" -> "application/scim+json","Location" -> (User.resource + user.id.get))
  }

}
