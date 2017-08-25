package controllers

import com.google.inject.Inject
import integration.DandelionIntegration
import io.swagger.annotations.{Api, ApiParam, ApiResponse, ApiResponses}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

@Api
 class Application @Inject() (dandelion: DandelionIntegration) extends Controller {

   def index = Action {
     Ok(views.html.index())
   }

   @ApiResponses(Array(
     new ApiResponse(code = 400, message = "Invalid text supplied"),
     new ApiResponse(code = 404, message = "Location not found")))
   def getLocationsByText(@ApiParam(value = "Text which locations should be extracted") text: String) = Action.async {

     val futureResult = dandelion.extractEntities(text)

     futureResult.map(i => Ok("Got result: " + dandelion.jsonParser(i.body).toString()))
   }
}