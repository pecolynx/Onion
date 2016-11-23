package models


import scalikejdbc._
import scalikejdbc.config.DBs

trait DBSettings {
  DBSettings.initialize()
}

object DBSettings {

  private var isInitialized = false

  def initialize(): Unit = this.synchronized {
    println("initlialize")
    if (isInitialized) return
    //DBs.setupAll()
    DBs.setup()
    //GlobalSettings.loggingSQLErrors = false
    //GlobalSettings.sqlFormatter = SQLFormatterSettings("utils.HibernateSQLFormatter")
    //DBInitializer.run()
    isInitialized = true
  }

}