package com.kujilabo.util

import com.typesafe.scalalogging.LazyLogging

object IntegerUtils extends LazyLogging {
  def toInt(value: String): Option[Int] = {
    try {
      Some(value.toInt)
    } catch {
      case e: Exception => {
        logger.warn("Failed to Int. value : <%s>".format(value))
        logger.warn(e.getStackTrace.mkString("\n"))
        None
      }
    }
  }
}
