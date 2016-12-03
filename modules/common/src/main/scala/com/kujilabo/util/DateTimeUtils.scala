package com.kujilabo.util

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateTimeUtils extends LazyLogging {

  val YEAR: String = "yyyy"
  val MONTH: String = "MM"
  val DAY: String = "dd"
  val HOUR: String = "HH"
  val MINUTE: String = "mm"
  val SECOND: String = "ss"

  def toDateTime(value: String, format: String): Option[DateTime] = {
    try {
      val formatter = DateTimeFormat.forPattern(format)
      Some(formatter.parseDateTime(value))
    } catch {
      case e: Exception => {
        logger.warn("Failed to DateTime. value : <%s>, format : <%s>".format(value, format))
        None
      }
    }
  }

  def toDateTime(value: String, formatter: DateTimeFormatter): Option[DateTime] = {
    try {
      Some(formatter.parseDateTime(value))
    } catch {
      case e: Exception => {
        logger.warn("Failed to DateTime. value : <%s>".format(value))
        logger.warn(value)
        None
      }
    }
  }

  def createFormatter(format: String): DateTimeFormatter = DateTimeFormat.forPattern(format)
}
