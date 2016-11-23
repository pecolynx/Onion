package utils

import com.typesafe.scalalogging.LazyLogging

object IntegerUtil extends LazyLogging {
  def toInt(value: String): Option[Int] = {
    try {
      Some(value.toInt)
    } catch {
      case e: Exception => {
        logger.warn(value)
        logger.warn(e.getStackTrace.mkString("\n"))
        None
      }
    }
  }
}
