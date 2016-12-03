package com.kujilabo.models.core

import javax.validation.{Configuration, ConstraintViolation, Validation}

import com.kujilabo.validation.ModelValidationException
import com.typesafe.scalalogging.LazyLogging

class BaseObject {
  //  extends LazyLogging {

  def validate(): Unit = {
    validate(this)
  }

  def validate[T](t: T): Unit = {
    val configuration = Validation.byDefaultProvider.configure
    val factory = configuration.asInstanceOf[Configuration[_]].buildValidatorFactory

    val validator = factory.getValidator

    val violations = validator.validate(t)
      .toArray(Array.empty[ConstraintViolation[Any]])

    if (violations.isEmpty) {
      return
    }

    val messageList = violations.map(x => x.getPropertyPath + " : " + x.getMessage)
    if (messageList.nonEmpty) {
      val message = messageList.mkString("\n")
      //logger.warn(message)
      throw new ModelValidationException(message)
    }
  }

}