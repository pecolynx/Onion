package utils

import com.fasterxml.jackson.databind.ObjectMapper

//import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Jsonユーティリティ。
  */
object JsonUtils {
  def toJson(obj: AnyRef): String = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)

    mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
      WRITE_DATES_AS_TIMESTAMPS, false)
    mapper.writeValueAsString(obj)

  }

  def toMap(json: String): Map[String, Any] = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    val map = mapper.readValue(json, classOf[Map[String, Any]])
    return map
  }


  def toObject[T](json: String, valueType: Class[T]): T = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    return mapper.readValue(json, valueType)
  }
}
