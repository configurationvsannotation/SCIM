package controllers

import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

class SCIMControllerSpec extends PlaySpec  with OneAppPerTest{

  "SCIMController" should {

    "respond with an user" in {
      val home = route(app, FakeRequest(GET, "/scim/v2/Users/123")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Your new application is ready.")
    }

  }

}
