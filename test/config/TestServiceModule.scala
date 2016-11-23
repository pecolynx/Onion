package config

import com.google.inject.AbstractModule
import models._

class TestServiceModule extends AbstractModule {
  override protected def configure() {
    println("configure")
    bind(classOf[AppSettings]).to(classOf[AppSettingsTestImpl])
  }

}
