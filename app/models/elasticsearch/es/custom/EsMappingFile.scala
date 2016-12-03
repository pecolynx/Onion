package models.elasticsearch.es.custom

import com.kujilabo.models.core.VariableName

object EsMappingFile {
  val FILE_NAME: VariableName = new VariableName("file_name")
  val FILE_PATH: VariableName = new VariableName("file_path")
  val FILE_CONTENT: VariableName = new VariableName("file_content")
  val FILE_EXT: VariableName = new VariableName("file_ext")
  val FILE_SIZE: VariableName = new VariableName("file_size")
  val FILE_CREATED_AT: VariableName = new VariableName("file_created_at")
  val FILE_UPDATED_AT: VariableName = new VariableName("file_updated_at")
}
