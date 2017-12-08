# TextGeoLocator

This API extracts a location for a given text.

## Build

Prerequisites: [scala](https://www.scala-lang.org/download/), [sbt](http://www.scala-sbt.org/download.html)

# BaseAddress
A URL base onde se encontra a raiz da nossa API
```
http://{{hostname}}:9000
```


## Endpoint

### Extract location

```
POST {{baseAddress}}/location
```

<strong>Request</strong>
<br>

| Name   |  Type     |  Description |
|--------|:---------:|:-------------:|
| Text   |  string   | Text that mentions a location to be extracted |

```json
{
  "Text": "A Universidade Federal Fluminense fica em Niterói."
}
```

<strong>Response</strong>
<br>

| Name        |      Type     |  Description |
|:------------|:-------------|:-------------:|
| cityName    | string  | City name, not present if only state is found  |
| cityID      | long    | City geoid, not present if only state is found |
| stateName   | string  | State name |
| countryName | string  | Country name |
| countryID   | long    | Country geoid|
| geoID       | long    | Geoid of the location entity |

```json
{
  "cityName": "Niterói", 
  "cityID": 3303302,
  "stateName": "Rio de Janeiro",
  "stateID": 33,
  "countryName": "Brazil",
  "geoID": "3303302",
  "countryID": 59470
}

```

### Run TextGeoLocator

To run TextGeoLocator you will need a [Dandelion API key](https://dandelion.eu/profile/dashboard/) to fill `dandelion.token` parameter on TextGeoLocator/conf/application.conf


You can run the following command in a separate command line window:
```
>cd TextGeoLocator
>sbt "run 9000"
```


### Acknowledgments

* Currently, there is support only for texts in Portuguese.
* Currently, there is support only for cities and states of Brazil.
* Currently, there is support only for shorts texts, location will be searched only for the first 2000 caracters.
* Users and developers are welcome to contact me through moniquemoledo@id.uff.br