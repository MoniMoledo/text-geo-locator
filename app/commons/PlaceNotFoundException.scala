package commons

/**
  * Created by monique on 19/09/17.
  */
case class PlaceNotFoundException(private val message: String = "",
                           private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
