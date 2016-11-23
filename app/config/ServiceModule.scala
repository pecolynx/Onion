package config

import com.google.inject.AbstractModule
import models.{AppSettings, AppSettingsImpl}

class ServiceModule extends AbstractModule {
  override protected def configure() {
    println("configure")
    bind(classOf[AppSettings]).to(classOf[AppSettingsImpl])
  }

}
