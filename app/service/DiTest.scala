package service

import javax.inject.Inject

import models.AppSettings

class DiTest {
  @Inject
  val appSettings: AppSettings = null

  def isNull(): Boolean = {
    this.appSettings == null
  }
}
