package controllers

import com.google.inject.Inject
import integration.DandelionIntegration
import io.swagger.annotations.{Api, ApiResponse, ApiResponses}
import map.Mapper
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

@Api
 class Application @Inject() (mapper: Mapper, dandelion: DandelionIntegration) extends Controller {

  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid text supplied"),
    new ApiResponse(code = 404, message = "Location not found")))
   def getLocationsByText() = Action.async {

     request =>
       val text = request.body.asJson.get.\("text").get.toString()
     val futureResult = dandelion.extractEntities(text)

     futureResult.map(extractedLocation => Ok(mapper.geoTag(dandelion.jsonParser(extractedLocation.body))))
   }
}