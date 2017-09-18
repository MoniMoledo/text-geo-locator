package integration

import com.google.inject.Inject
import commons.GeoLevel.GeoLevel
import commons.{DandelionResult, GeoLevel}
import play.api.Configuration
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.Controller

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
  * Created by monique on 16/08/17.
  */
class DandelionIntegration @Inject() (ws: WSClient, config: Configuration) extends Controller{

  def extractEntities(text : String): Future[WSResponse] = {

    val baseAddress = "https://api.dandelion.eu/datatxt/nex/v1/"

    val lang = "?lang=pt &"

    val maxLength = if (text.length < 1612) text.length else 1612
    val textParam = "text=" + text.substring(0, maxLength) + "\" &"

    val include = "include=types &"

    val token = "token=" + config.get[String]("dandelion.token")

    val url = baseAddress + lang + textParam + include + token

    val request: WSRequest = ws.url(url)

    val futureResult: Future[WSResponse] = request.get()

    return futureResult
  }

  def jsonParser(text : String): DandelionResult = {

    val json = Json.parse(text)

    val annotations = (json \ "annotations").as[JsArray]
    var places = new ListBuffer[(JsValue, GeoLevel)]();

    for(annotation <- annotations.value) {

      val typeJsArray = (annotation \\ "types")

      val cities = typeJsArray.filter(_.toString().contains("City"))
      val states = typeJsArray.filter(_.toString().contains("AdministrativeRegion"))
      val countries = typeJsArray.filter(_.toString().contains("Country"))

      if(cities.nonEmpty) {
        places.append((annotation, GeoLevel.City))
      }
      else if(states.nonEmpty) {
        places.append((annotation, GeoLevel.State))
      }
      else if(countries.nonEmpty) {
        places.append((annotation, GeoLevel.Country))
      }
    }

    if(places.nonEmpty){
      val maxConfidencePlace = places.toList.maxBy(_._1.\("confidence").as[Float])
      return new DandelionResult(maxConfidencePlace._1.\("label").as[String], maxConfidencePlace._2)
    }
    return new DandelionResult("No place found", GeoLevel.Default)
  }
}
