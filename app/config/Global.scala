package config

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.typesafe.scalalogging.LazyLogging
import models.AppSettingsImpl
import models.elasticsearch.db.DbFieldType.DbFieldTypeText
import models.elasticsearch.es.EsFieldAnalyzer.{EsFieldAnalyzerKuromoji, EsFieldAnalyzerNone}
import models.elasticsearch.es.EsFieldFormat.{EsFieldFormatDateOptionalTime, EsFieldFormatNone}
import models.elasticsearch.es.EsFieldType.{EsFieldTypeDate, EsFieldTypeInteger, EsFieldTypeString}
import models.elasticsearch.es.custom.EsMappingFile
import models.elasticsearch.{Field, FieldList, Index, Mapping}
import play.api.inject.ApplicationLifecycle
import scalikejdbc.ConnectionPool
import service.elasticsearch.{IndexService, MappingService}
import service.elasticsearch.db.DbIndexService
import service.elasticsearch.es.EsIndexService

//@Singleton
class Global @Inject()(system: ActorSystem, lifecycle: ApplicationLifecycle) extends LazyLogging {
  onStart()

  def onStart(): Unit = {
    logger.info("info")
    logger.warn("warn")

    val settings = scalikejdbc.config.DBs.readJDBCSettings('default)
    ConnectionPool.singleton(settings.url, settings.user, settings.password)

    val appSettings = new AppSettingsImpl


    if (DbIndexService.containsModel(appSettings.fileIndex())) {
      val dbIndex = DbIndexService.getModelByKey(appSettings.fileIndex())
      DbIndexService.removeModel(dbIndex.id, dbIndex.version)
    }

    if (EsIndexService.containsIndex(appSettings.esUrl(), appSettings.fileIndex())) {
      EsIndexService.removeIndex(appSettings.esUrl, appSettings.fileIndex())
    }

    IndexService.addIndex(appSettings.esUrl, new Index(appSettings.fileIndex()))
    val index = IndexService.getIndex(appSettings.esUrl, appSettings.fileIndex())
    MappingService.addMapping(appSettings.esUrl, appSettings.fileIndex(), new Mapping(index,
      appSettings.fileMapping))

    val mapping = MappingService.getMapping(appSettings.esUrl, appSettings.fileIndex(), appSettings.fileMapping)
    val userFieldList = new FieldList(List(
      new Field(
        EsMappingFile.FILE_PATH, "ファイルパス", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 1),
      new Field(
        EsMappingFile.FILE_NAME, "ファイル名", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 2),
      new Field(
        EsMappingFile.FILE_CONTENT, "内容", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 3),
      new Field(
        EsMappingFile.FILE_EXT, "拡張子", true, true, true,
        EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji,
        DbFieldTypeText, 4),
      new Field(
        EsMappingFile.FILE_SIZE, "ファイルサイズ", true, true, true,
        EsFieldTypeInteger, EsFieldFormatNone, EsFieldAnalyzerNone,
        DbFieldTypeText, 5),
      new Field(
        EsMappingFile.FILE_CREATED_AT, "ファイル作成日時", true, true, true,
        EsFieldTypeDate, EsFieldFormatDateOptionalTime, EsFieldAnalyzerNone,
        DbFieldTypeText, 6),
      new Field(
        EsMappingFile.FILE_UPDATED_AT, "ファイル更新日時", true, true, true,
        EsFieldTypeDate, EsFieldFormatDateOptionalTime, EsFieldAnalyzerNone,
        DbFieldTypeText, 7)
    ))
    MappingService.updateMapping(appSettings.esUrl, index, mapping, userFieldList)
  }
}

class GlobalModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[Global]).asEagerSingleton()
}