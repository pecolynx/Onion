package models

import com.kujilabo.models.core.VariableName
import models.elasticsearch.{IndexName, MappingName}

class AppSettingsTestImpl extends AppSettings {
  override def esUrl(): String = "http://192.168.56.20:9200"

  override def esAddress(): String = "192.168.56.20"

  override def esPort(): Int = 9300

  override def fileIndex(): IndexName = new IndexName(new VariableName("test_file_index"))

  override def fileMapping(): MappingName = new MappingName(new VariableName("test_file_mapping"))

  override def authTokenExpiresDays: Int = 1
}
