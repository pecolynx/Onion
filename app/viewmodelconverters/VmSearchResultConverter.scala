package viewmodelconverters

import com.kujilabo.models.elasticsearch.SearchResult
import viewmodels.VmSearchResult

/**
  * 検索結果ビューモデル変換クラス。
  */
object VmSearchResultConverter {

  def toViewModel(searchResult: SearchResult): VmSearchResult = {
    new VmSearchResult(searchResult.hitList.map(VmHitConverter.toViewModel),
      searchResult.totalCount)
  }
}
