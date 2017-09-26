package controllers

import com.google.inject.Inject
import integration.DandelionIntegration
import map.Mapper
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

 class Application @Inject() (mapper: Mapper, dandelion: DandelionIntegration) extends Controller {

   def getLocationsByText() = Action.async(parse.json(maxLength = 1024 * 500)) {

     request =>
       val text = request.body.\("text").get.toString()
       val futureResult = dandelion.extractEntities(text)

       futureResult.map(extractedLocation => Ok(mapper.geoTag(dandelion.jsonParser(extractedLocation.body))))
   }
}