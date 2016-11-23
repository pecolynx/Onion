package models.elasticsearch.db

sealed abstract class DbFieldType(val id: Int)

object DbFieldType {

  case object DbFieldTypeText extends DbFieldType(1)

}
