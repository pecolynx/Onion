package controllers

import config.Constants
import play.api.mvc.Action
import utils.JsonUtils

class IndexController extends ControllerBase {
  def index() = Action { request =>
    val result = Map("version" -> Constants.VERSION)
    Ok(JsonUtils.toJson(result))
  }
}
