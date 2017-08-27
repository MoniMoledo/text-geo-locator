package integration

import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.Controller

import scala.concurrent.Future

/**
  * Created by monique on 16/08/17.
  */
class DandelionIntegration @Inject() (ws: WSClient, config: Configuration) extends Controller{

  def extractEntities(text : String): Future[WSResponse] = {

    val baseAddress = "https://api.dandelion.eu/datatxt/nex/v1/"

    val lang = "?lang=pt &"

    val textParam = "text=" + text + " &"

    val include = "include=types &"

    val token = "token=" + config.get[String]("dandelion.token")

    val url = baseAddress + lang + textParam + include + token

    val request: WSRequest = ws.url(url)

    val futureResult: Future[WSResponse] = request.get()

    return futureResult
  }

  def jsonParser(text : String): (String, String) = {

    val json = Json.parse(text)

    val annotations = (json \ "annotations").as[JsArray]

    for(annotation <- annotations.value){

      val typeJsArray =  (annotation \\ "types")
      val locationTitle = (annotation \ "title").as[String]

      for(typeValue <- typeJsArray) {
        if(typeValue.toString().contains("City"))  return (locationTitle, "City")
        if(typeValue.toString().contains("AdministrativeRegion")) return (locationTitle, "State")
        if(typeValue.toString().contains("Country"))   return (locationTitle, "Country")
      }
    }

    return ("No place found", "Not a place")
  }
}
