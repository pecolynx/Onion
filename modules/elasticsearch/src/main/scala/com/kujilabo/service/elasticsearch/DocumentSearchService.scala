package com.kujilabo.service.elasticsearch

import com.kujilabo.models.core.Page
import com.kujilabo.models.elasticsearch.{EsDocumentBuilderOld, IndexName, MappingName, SearchResult}
import com.kujilabo.service.elasticsearch.es.EsMappingService
import com.typesafe.scalalogging.LazyLogging
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.{FilterBuilder, FilterBuilders, QueryBuilders}
import org.elasticsearch.index.query.MatchQueryBuilder.Operator
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.{SearchHit, SearchHitField}


object DocumentSearchService extends LazyLogging {

  def searchByKeyword
  (
    esAddress: String, esPort: Int, esUrl: String,
    indexName: IndexName, mappingName: MappingName,
    page: Page, keywordList: Seq[String],
    createdBy: Option[Int]
  ): SearchResult = {
    val client = new TransportClient()
    client.addTransportAddress(new InetSocketTransportAddress(esAddress, esPort))
    val srb = client.prepareSearch(indexName.value.value)
    srb.setTypes(mappingName.value.value)
    srb.setVersion(true)

    srb.addField("*").setFrom((page.page - 1) * page.size).setSize(page.size)

    srb.addHighlightedField("title")
    srb.addHighlightedField("*")
    srb.addHighlightedField("_all")
    srb.setHighlighterPreTags("<mark>")
    srb.setHighlighterPostTags("</mark>")

    val isEmpty = keywordList.isEmpty || (keywordList.length == 1 && keywordList(0) == "")
    if (!isEmpty) {
      val query = QueryBuilders.filteredQuery(
        QueryBuilders.matchQuery("_all", keywordList.mkString(" ")).operator(Operator.AND),
        this.getFilterBuilder(keywordList.mkString(" ")))

      srb.setQuery(query)
    }

    srb.addSort(SortBuilders.scoreSort())

    val response = srb.execute().actionGet()
    println(response)
    def getMapping(indexName: IndexName, mappingName: MappingName) = {
      EsMappingService.getMapping(esUrl, indexName, mappingName)
    }

    return new SearchResult(new EsDocumentBuilderOld().buildHitList(response, getMapping),
      response.getHits().getTotalHits())
  }

  def getFilterBuilder(keyword: String): FilterBuilder = {
    logger.warn("keyword = " + keyword)
    val and = FilterBuilders.andFilter()
    if (keyword != "") {
      and.add(FilterBuilders.queryFilter(QueryBuilders.matchPhraseQuery("_all", keyword)))
    }

    return and
  }

}
