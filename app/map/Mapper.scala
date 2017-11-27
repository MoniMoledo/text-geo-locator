package map

import commons.{DandelionResult, GeoLevel, PlaceNotFoundException, PlaceNotMappedException}
import play.api.libs.json.{JsArray, JsValue}

class Mapper {

  @throws(classOf[Exception])
  def geoTag(place : DandelionResult): JsValue = {
    val name = place.getPlaceName
    val level = place.getPlaceLevel

    level match {
      case GeoLevel.City => return tagCity(name)
      case GeoLevel.State => return tagState(name)
      case GeoLevel.Country => return tagCountry(name)
      case GeoLevel.Default => throw new PlaceNotFoundException("No place was found for the given text")
    }
  }

  @throws(classOf[Exception])
  def tagCity(name: String): JsValue = {

    //TODO use a HashSet of cities
    val cities = (PlaceJsonFile.cityJson \ "features").as[JsArray]

    for (city <- cities.value) {
      val cityProperties = (city \ "properties").as[JsValue]
      val cityName = (cityProperties \ "cityName").as[String]
      if (cityName.matches(name)) return cityProperties
    }

    throw new PlaceNotMappedException("City: " + name + " could not be mapped")
  }

  @throws(classOf[Exception])
  def tagState(name: String): JsValue = {

    val states = (PlaceJsonFile.stateJson \ "features").as[JsArray]

    for (state <- states.value) {
      val stateProperties = (state \ "properties").as[JsValue]
      val stateName = (stateProperties \ "stateName").as[String]
      if (stateName.matches(name)) { return stateProperties }
    }

    throw new PlaceNotMappedException("State: " + name + " could not be mapped")
  }

  @throws(classOf[Exception])
  def tagCountry(name: String): JsValue = {
    val countries = (PlaceJsonFile.countryJson \ "features").as[JsArray]

    for (country <- countries.value) {
      val countryProperties = (country \ "properties").as[JsValue]
      val countryName = (countryProperties \ "countryName").as[String]
      if (countryName.matches(name)) return countryProperties
    }

    throw new PlaceNotMappedException("Country: " + name + " could not be mapped")
  }
}
