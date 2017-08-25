package integration

import com.google.inject.Inject
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsObject, _}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.Controller

import scala.concurrent.Future

/**
  * Created by monique on 16/08/17.
  */
class DandelionIntegration @Inject() (ws: WSClient) extends Controller{

  def extractEntities(text : String): Future[WSResponse] = {

    val baseAddress = "https://api.dandelion.eu/datatxt/nex/v1/"

    val lang = "?lang=pt &"

    val textParam = "text=" + text + " &"

    val include = "include=types &"

    val token = "token=9efafad033534968b3ef537caff747d3"

    val url = baseAddress + lang + textParam + include + token

    val request: WSRequest = ws.url(url)

    val futureResult: Future[WSResponse] = request.get()

    return futureResult
  }

  def jsonParser(text : String): String = {

    val json = Json.parse(text)

    val annotations = (json \ "annotations")
    val allTypes = annotations.\\("types")
    val entities = annotations.\\("spot")

    for(typeList <- allTypes){
      for(e <- entities){
       val typeJsArray = typeList.as[JsArray].value
       for(typeValue <- typeJsArray) {
         if (typeValue.toString().contains("Location")) {
           return e.toString()
          }
        }
      }
    }
    return "no place found =("
  }
}
