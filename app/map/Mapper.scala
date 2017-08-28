package map

import commons.{DandelionResult, GeoLevel}
import commons.GeoLevel.GeoLevel
import play.api.libs.json.{JsArray, JsValue, Json}

/**
  * Created by monique on 27/08/17.
  */
class Mapper {


  def geoTag(place : DandelionResult): JsValue = {
    val name = place.getPlaceName
    val level = place.getPlaceLevel

    level match {
      case GeoLevel.City => return tagCity(name)
      case GeoLevel.State => return tagState(name)
      case GeoLevel.Country => return tagCountry(name)
    }
  }

  @throws(classOf[Exception])
  def tagCity(name: String): JsValue = {

    val cities = (PlaceJsonFile.cityJson \ "features").as[JsArray]

    for (city <- cities.value) {
      val cityProperties = (city \ "properties").as[JsValue]
      val cityName = (cityProperties \ "name").as[String]
      if (cityName.matches(name)) return cityProperties
    }

    throw new Exception("City: " + name + " could not be tagged")
  }

  @throws(classOf[Exception])
  def tagState(name: String): JsValue = {

    val states = (PlaceJsonFile.stateJson \ "features").as[JsArray]

    for (state <- states.value) {
      val stateProperties = (state \ "properties").as[JsValue]
      val stateName = (stateProperties \ "name").as[String]
      if (stateName.matches(name)) return stateProperties
    }

    throw new Exception("State: " + name + " could not be tagged")
  }

  @throws(classOf[Exception])
  def tagCountry(name: String): JsValue = {
    val countries = (PlaceJsonFile.countryJson \ "features").as[JsArray]

    for (country <- countries.value) {
      val countryProperties = (country \ "properties").as[JsValue]
      val countryName = (countryProperties \ "name").as[String]
      if (countryName.matches(name)) return countryProperties
    }

    throw new Exception("Country: " + name + " could not be tagged")
  }
}
