package controllers

import com.kujilabo.util.JsonUtils
import config.Constants
import play.api.mvc.Action

class IndexController extends ControllerBase {
  def index() = Action { request =>
    val result = Map("version" -> Constants.VERSION)
    Ok(JsonUtils.toJson(result))
  }
}
