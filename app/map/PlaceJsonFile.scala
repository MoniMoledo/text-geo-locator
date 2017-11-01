package map

import java.io.File
import java.nio.charset.{Charset, CodingErrorAction}

import play.api.libs.json.{JsValue, Json}

import scala.io.Source

object PlaceJsonFile {

  val cityJson: JsValue = Json.parse(loadSmallJSONFile(new File("public/data/br_city_hierarchy.json")))

  val stateJson: JsValue = Json.parse(loadSmallJSONFile(new File("public/data/br_state_hierarchy.json")))

  val countryJson: JsValue = Json.parse(loadSmallJSONFile(new File("public/data/br_country_hierarchy.json")))


  def loadSmallJSONFile(file: File): String = {
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    Source.fromFile(file)(decoder).getLines().mkString("\n")
  }
}
