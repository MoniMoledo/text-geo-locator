name := "TextGeoLocator"

version := "1.0"

lazy val `textgeolocator` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws , specs2 % Test , guice)

libraryDependencies ++= Seq(
  "io.swagger" %% "swagger-play2" % "1.6.1-SNAPSHOT"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "maven" at "https://mvnrepository.com/"