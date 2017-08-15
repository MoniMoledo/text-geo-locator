package controllers

import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import play.api.mvc._

@Api
 class Application extends Controller {

   def index = Action {
     Ok(views.html.index())
   }

   @ApiResponses(Array(
     new ApiResponse(code = 400, message = "Invalid text supplied"),
     new ApiResponse(code = 404, message = "Location not found")))
   def getLocationsByText(@ApiParam(value = "Text which locations should be extracted") text: String) = Action {
     implicit request =>
       text match {
         case "London" => Ok("Londres!")
         case _ => BadRequest("Not Londres!")
       }
   }
}