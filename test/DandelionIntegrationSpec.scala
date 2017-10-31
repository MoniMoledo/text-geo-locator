  import commons.GeoLevel
  import integration.DandelionIntegration
  import org.specs2.mutable._
  import org.specs2.runner._
  import org.junit.runner._
  import org.specs2.mock.Mockito
  import play.api.Configuration
  import play.api.libs.ws.WSClient
  
  @RunWith(classOf[JUnitRunner])
  class DandelionIntegrationSpec extends Specification with Mockito {

    "DandelionIntegration" should {

      "build request without special characters" in {
        val wsMock = mock[WSClient]
        val configMock = mock[Configuration]
        val dandelionIntegration = new DandelionIntegration(wsMock, configMock)

        dandelionIntegration.buildDandelionRequest("M@gi das #cruzes custa% $20 reais") must beEqualTo("https://api.dandelion.eu/datatxt/nex/v1/?lang=pt &text=Mgi das cruzes custa 20 reais &include=types &token=null")
      }

      "build correct request" in {
        val wsMock = mock[WSClient]
        val configMock = mock[Configuration]
        val dandelionIntegration = new DandelionIntegration(wsMock, configMock)

        dandelionIntegration.buildDandelionRequest("Niterói") must beEqualTo("https://api.dandelion.eu/datatxt/nex/v1/?lang=pt &text=Niterói &include=types &token=null")
      }

      "parse dandelion result" in {
        val wsMock = mock[WSClient]
        val configMock = mock[Configuration]

        val dandelionIntegration = new DandelionIntegration(wsMock, configMock)

        val rawResult = "{\"time\":0,\"annotations\":[{\"start\":0,\"end\":9,\"spot\":\"Tocantins\",\"confidence\":0.8544,\"id\":1825,\"title\":\"Tocantins\",\"uri\":\"http://pt.wikipedia.org/wiki/Tocantins\",\"label\":\"Tocantins\",\n\"types\":[\"http://dbpedia.org/ontology/AdministrativeRegion\",\n\"http://dbpedia.org/ontology/\\u003chttp://purl.org/dc/terms/Jurisdiction\\u003e\",\n\"http://dbpedia.org/ontology/Region\",\n\"http://dbpedia.org/ontology/PopulatedPlace\",\n\"http://dbpedia.org/ontology/Place\",\n\"http://dbpedia.org/ontology/Location\"]}],\n\"lang\":\"pt\",\"timestamp\":\"2017-08-25T05:33:28.060\"}"

        val actualResult = dandelionIntegration.parseDandelionResult(rawResult)
        actualResult.getPlaceName must beEqualTo("Tocantins")
        actualResult.getPlaceLevel must beEqualTo(GeoLevel.State)

        val rawResult2 = "{\"time\":0,\"annotations\":[{\"start\":0,\"end\":7,\"spot\":\"Londres\",\"confidence\":0.8079,\"id\":1197,\"title\":\"Londres\",\"uri\":\"http://pt.wikipedia.org/wiki/Londres\",\"label\":\"Londres\",\n\"types\":[\n\"http://dbpedia.org/ontology/City\",\n\"http://dbpedia.org/ontology/Settlement\",\n\"http://dbpedia.org/ontology/PopulatedPlace\",\n\"http://dbpedia.org/ontology/Place\",\n\"http://dbpedia.org/ontology/Location\"]}],\n\"lang\":\"pt\",\"timestamp\":\"2017-08-25T05:33:28.060\"}"

        val actualResult2 = dandelionIntegration.parseDandelionResult(rawResult2)
        actualResult2.getPlaceName must beEqualTo("Londres")
        actualResult2.getPlaceLevel must beEqualTo(GeoLevel.City)
      }

      "get minimun brazilian Geo Level in the result" in {

        val wsMock = mock[WSClient]
        val configMock = mock[Configuration]

        val dandelionIntegration = new DandelionIntegration(wsMock, configMock)

        val rawResult = "{\"time\":2,\"annotations\":[{\"start\":1,\"end\":7,\"spot\":\"Brasil\",\"confidence\":0.911,\"id\":404,\"title\":\"Brasil\",\"uri\":\"http://pt.wikipedia.org/wiki/Brasil\",\"label\":\"Brasil\",\"types\":[]},{\"start\":9,\"end\":25,\"spot\":\"DIstrito Federal\",\"confidence\":0.8799,\"id\":684,\"title\":\"Distrito Federal (Brasil)\",\"uri\":\"http://pt.wikipedia.org/wiki/Distrito_Federal_%28Brasil%29\",\"label\":\"Distrito Federal\",\"types\":[\"http://dbpedia.org/ontology/AdministrativeRegion\",\"http://dbpedia.org/ontology/\\u003chttp://purl.org/dc/terms/Jurisdiction\\u003e\",\"http://dbpedia.org/ontology/Region\",\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]},{\"start\":27,\"end\":36,\"spot\":\"São Paulo\",\"confidence\":0.8902,\"id\":1719,\"title\":\"São Paulo\",\"uri\":\"http://pt.wikipedia.org/wiki/S%C3%A3o_Paulo\",\"label\":\"São Paulo\",\"types\":[\"http://dbpedia.org/ontology/AdministrativeRegion\",\"http://dbpedia.org/ontology/\\u003chttp://purl.org/dc/terms/Jurisdiction\\u003e\",\"http://dbpedia.org/ontology/Region\",\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]},{\"start\":38,\"end\":52,\"spot\":\"Estados Unidos\",\"confidence\":0.8262,\"id\":790,\"title\":\"Estados Unidos\",\"uri\":\"http://pt.wikipedia.org/wiki/Estados_Unidos\",\"label\":\"Estados Unidos\",\"types\":[\"http://dbpedia.org/ontology/Country\",\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]},{\"start\":54,\"end\":61,\"spot\":\"Niterói\",\"confidence\":0.8915,\"id\":6638,\"title\":\"Niterói\",\"uri\":\"http://pt.wikipedia.org/wiki/Niter%C3%B3i\",\"label\":\"Niterói\",\"types\":[\"http://dbpedia.org/ontology/City\",\"http://dbpedia.org/ontology/Settlement\",\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]}],\"lang\":\"pt\",\"timestamp\":\"2017-10-31T13:09:17.141\"}"

        val actualResult = dandelionIntegration.parseDandelionResult(rawResult)
        actualResult.getPlaceName must beEqualTo("Niterói")
        actualResult.getPlaceLevel must beEqualTo(GeoLevel.City)

        val rawResult2 = "{\"time\":1,\"annotations\":[{\"start\":1,\"end\":8,\"spot\":\"Londres\",\"confidence\":0.8645,\"id\":1197,\"title\":\"Londres\",\"uri\":\"http://pt.wikipedia.org/wiki/Londres\",\"label\":\"Londres\",\"types\":[\"http://dbpedia.org/ontology/City\",\"http://dbpedia.org/ontology/Settlement\",\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]},{\"start\":10,\"end\":23,\"spot\":\"San Francisco\",\"confidence\":0.8245,\"id\":22444,\"title\":\"São Francisco (Califórnia)\",\"uri\":\"http://pt.wikipedia.org/wiki/S%C3%A3o_Francisco_%28Calif%C3%B3rnia%29\",\"label\":\"São Francisco\",\"types\":[\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]},{\"start\":25,\"end\":33,\"spot\":\"Montreal\",\"confidence\":0.8753,\"id\":26094,\"title\":\"Montreal\",\"uri\":\"http://pt.wikipedia.org/wiki/Montreal\",\"label\":\"Montreal\",\"types\":[\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]},{\"start\":35,\"end\":41,\"spot\":\"Ottawa\",\"confidence\":0.9106,\"id\":26604,\"title\":\"Ottawa\",\"uri\":\"http://pt.wikipedia.org/wiki/Ottawa\",\"label\":\"Ottawa\",\"types\":[\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]},{\"start\":43,\"end\":48,\"spot\":\"Amapá\",\"confidence\":0.8701,\"id\":297,\"title\":\"Amapá\",\"uri\":\"http://pt.wikipedia.org/wiki/Amap%C3%A1\",\"label\":\"Amapá\",\"types\":[\"http://dbpedia.org/ontology/AdministrativeRegion\",\"http://dbpedia.org/ontology/\\u003chttp://purl.org/dc/terms/Jurisdiction\\u003e\",\"http://dbpedia.org/ontology/Region\",\"http://dbpedia.org/ontology/PopulatedPlace\",\"http://dbpedia.org/ontology/Place\",\"http://dbpedia.org/ontology/Location\"]}],\"lang\":\"pt\",\"timestamp\":\"2017-10-31T13:17:30.418\"}"

        val actualResult2 = dandelionIntegration.parseDandelionResult(rawResult2)
        actualResult2.getPlaceName must beEqualTo("Amapá")
        actualResult2.getPlaceLevel must beEqualTo(GeoLevel.State)
      }
  }
}
