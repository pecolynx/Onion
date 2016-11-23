package viewmodelconverters

import models.core.{ModelIdT, Title}
import models.elasticsearch.es.custom.EsMappingFile
import models.elasticsearch.{IndexName, MappingName}
import models.elasticsearch.es.{EsDocument, EsDocumentFieldList, EsDocumentFieldString, EsMapping}
import org.joda.time.DateTime
import viewmodels.{VmDocument, VmDocumentFile}

/**
  * ドキュメントビューモデル変換クラス。
  */
object VmDocumentConverter {

  def toModelForAdd(vm: VmDocumentFile, indexName: IndexName, mappingName: MappingName,
                    createdBy: ModelIdT[Int]): EsDocument = {
    new EsDocument(indexName, mappingName, new Title(vm.fileName), createdBy.value,
      vm.documentFieldList)
  }

  /**
    * ビューモデルを更新用のモデルに変換します。<br />
    * タイトル、フィールド以外は任意に変更できません。
    *
    * @param model 旧モデル
    * @param vm    新ビューモデル
    * @return 新モデル
    */
  def toModelForUpdate(model: EsDocument, vm: VmDocumentFile): EsDocument = {
    new EsDocument(model.id, model.version, model.createdAt, new DateTime(),
      model.indexName, model.mappingName, new Title(vm.fileName), model.createdBy,
      vm.documentFieldList)
  }

  def toViewModel(document: EsDocument): VmDocument = {
    new VmDocument(document.id.value, document.version,
      document.createdAt.toString("yyyy/MM/dd HH:mm:ss"),
      document.updatedAt.toString("yyyy/MM/dd HH:mm:ss"),
      document.indexName.value.value, document.mappingName.value.value,
      document.title.value,
      document.documentFieldList.list.toList.map(VmDocumentFieldConverter.toViewModel))
  }
}
