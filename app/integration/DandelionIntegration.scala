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
class DandelionIntegration @Inject() (ws: WSClient, config: Configuration) {

  def buildDandelionRequest(text: String): String = {

    //Dandelion API only accepts short texts and doesn't support some special characters
    val filteredText = removeSpecialCharacters(text).take(2500)

    val baseAddress = "https://api.dandelion.eu/datatxt/nex/v1/"

    val portuguese = "pt"

    val token = config.get[String]("dandelion.token")

    s"$baseAddress?lang=$portuguese &text=$filteredText &include=types &token=$token"
  }

  def extractEntities(text : String): Future[WSResponse] = {

    val url = buildDandelionRequest(text)

    val request: WSRequest = ws.url(url)

    val futureResult: Future[WSResponse] = request.get()

    return futureResult

  }

  private def removeSpecialCharacters(text: String): String = {
    text.replaceAll("[@#$%*]", "")
  }

  def parseDandelionResult(text : String): DandelionResult = {

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
      else if(countries.nonEmpty && annotation.\("label").as[String] == "Brasil") {
        places.append((annotation, GeoLevel.Country))
      }
    }

    //TODO: If city can not be mapped, states and countries that could are wasted
    if(places.nonEmpty){
      val maxConfidencePlace = places.toList.maxBy(_._1.\("confidence").as[Float])
      return new DandelionResult(maxConfidencePlace._1.\("label").as[String], maxConfidencePlace._2)
    }
    return new DandelionResult("No place found", GeoLevel.Default)
  }
}
