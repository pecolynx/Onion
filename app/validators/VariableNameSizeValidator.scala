package validators

import javax.validation.{ConstraintValidator, ConstraintValidatorContext}

import models.core.VariableName

class VariableNameSizeValidator extends ConstraintValidator[VariableNameSize, VariableName] {

  var min: Int = _
  var max: Int = _

  override def initialize(constraintAnnotation: VariableNameSize): Unit = {
    this.min = constraintAnnotation.min()
    this.max = constraintAnnotation.max()
  }

  override def isValid(value: VariableName, context: ConstraintValidatorContext): Boolean = {
    if (value == null) {
      return true
    }

    val length = value.value.length()
    if (this.max < length || length < this.min) {
      return false
    }

    return true
  }
}
