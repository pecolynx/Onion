package viewmodelconverters

import com.kujilabo.models.core.Page
import com.kujilabo.models.elasticsearch.FileSearchCondition
import viewmodels.file.VmSearchCondition

/**
  * ファイル検索条件ビューモデル変換クラス。
  */
object VmFileSearchConditionConverter {
  def toModel(vm: VmSearchCondition): FileSearchCondition = {
    new FileSearchCondition(new Page(vm.pageNumber, vm.pageSize), vm.keywordList, vm.extList)
  }
}
