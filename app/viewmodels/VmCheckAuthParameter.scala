package viewmodels

import javax.validation.constraints.Size

import config.Constants
import models.core.BaseObject
import models.exceptions.BadRequestException

import scala.annotation.meta.field

class VmCheckAuthParameter
(
  @(Size@field)(min = Constants.LOGIN_ID_LENGTH_MIN, max = Constants.LOGIN_ID_LENGTH_MAX)
  val loginId: String,
  @(Size@field)(min = Constants.AUTH_TOKEN_LENGTH_MIN, max = Constants.AUTH_TOKEN_LENGTH_MAX)
  val authToken: String
) extends BaseObject {
  this.validate
}
