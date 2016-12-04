package viewmodels

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonIgnoreProperties}
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.es.custom.EsMappingFile
import com.kujilabo.models.elasticsearch.es.{EsDocumentFieldDateTime, EsDocumentFieldInteger, EsDocumentFieldList, EsDocumentFieldString}
import org.joda.time.DateTime
import com.kujilabo.util.DateTimeUtils

/**
  *
  * @param id
  * @param fileName
  * @param filePath
  * @param fileContent
  */
@JsonIgnoreProperties(ignoreUnknown = true)
class VmDocumentFile
(
  val id: String,
  val fileName: String,
  val filePath: String,
  val fileContent: String,
  val fileHash: String,
  val fileSize: Int,
  val fileCreatedAt: String,
  val fileUpdatedAt: String
) {

  val DATE_TIME_FORMAT =
    DateTimeUtils.YEAR + "/" + DateTimeUtils.MONTH + "/" + DateTimeUtils.DAY + " " +
      DateTimeUtils.HOUR + ":" + DateTimeUtils.MINUTE + ":" + DateTimeUtils.SECOND

  def this
  (
    fileName: String,
    filePath: String,
    fileContent: String,
    fileHash: String,
    fileSize: Int,
    fileCreatedAt: String,
    fileUpdatedAt: String
  ) {
    this(
      "",
      fileName,
      filePath,
      fileContent,
      fileHash,
      fileSize,
      fileCreatedAt,
      fileUpdatedAt
    )
  }

  def createDocumentFieldDateTime(fieldName: VariableName, value: String): EsDocumentFieldDateTime = {
    new EsDocumentFieldDateTime(
      fieldName,
      DateTimeUtils.toDateTime(fileCreatedAt, DATE_TIME_FORMAT).getOrElse(new DateTime())
    )
  }

  @JsonIgnore
  val documentFieldList: EsDocumentFieldList = new EsDocumentFieldList(List(
    new EsDocumentFieldString(EsMappingFile.FILE_NAME, fileName),
    new EsDocumentFieldString(EsMappingFile.FILE_PATH, filePath),
    new EsDocumentFieldString(EsMappingFile.FILE_CONTENT, fileContent),
    new EsDocumentFieldInteger(EsMappingFile.FILE_SIZE, fileSize),
    createDocumentFieldDateTime(EsMappingFile.FILE_CREATED_AT, fileCreatedAt),
    createDocumentFieldDateTime(EsMappingFile.FILE_UPDATED_AT, fileUpdatedAt)
  ))
}
