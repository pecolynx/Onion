package service.elasticsearch

import com.typesafe.scalalogging.LazyLogging
import models.core.Page
import models.elasticsearch._
import org.elasticsearch.action.search.{SearchRequestBuilder, SearchType}
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.MatchQueryBuilder.Operator
import org.elasticsearch.index.query._
import org.elasticsearch.search.sort.SortBuilders
import service.elasticsearch.es.EsMappingService
import utils.JsonUtils

object DocumentFileSearchServiceV1 extends LazyLogging {

  def search(esAddress: String, esPort: Int, esUrl: String,
             indexName: IndexName, mappingName: MappingName, createdBy: Int,
             searchCondition: FileSearchCondition): SearchResult = {
    val client = new TransportClient()
    client.addTransportAddress(new InetSocketTransportAddress(esAddress, esPort))
    val srb = client.prepareSearch(indexName.value.value)
    srb.setTypes(mappingName.value.value)
    srb.setVersion(true)
    //srb.setSearchType(SearchType.QUERY_THEN_FETCH)

    this.setPageToSearchRequestBuilder(srb, searchCondition.page)
    this.setHighlightToSearchRequestBuilder(srb)
    this.setQueryToSearchRequestBuilder(srb, createdBy, searchCondition.keywordList, searchCondition
      .extList)
    this.setSortToSearchRequestBuilder(srb)

    val request = JsonUtils.toJson(DocumentFileSearchRequest.getMap(createdBy,
      searchCondition.keywordList.mkString(" "),
      searchCondition.page.page - 1, searchCondition.page.size))
    //val response2 = RestClient.post(esUrl + "/file_index/_search", request)

    logger.warn("request : " + request)
    //logger.warn("response : " + response2.toString().substring(0, 1000))
    val response = srb.execute().actionGet()
    //logger.info("response : " + response.toString)
    logger.warn("took : " + response.getTookInMillis)
    logger.warn("response length : " + response.toString().length())

    val fileMapping = EsMappingService.getMapping(esUrl, indexName, mappingName)
    def getMapping(indexName: IndexName, mappingName: MappingName) = {
      fileMapping
    }

    return new SearchResult(new EsDocumentBuilderOld().buildHitList(response, getMapping),
      response.getHits().getTotalHits())
  }

  def setPageToSearchRequestBuilder(srb: SearchRequestBuilder, page: Page) = {

    srb.addField("title")
    srb.addField("file_path")
    srb.addField("created_at")
    srb.addField("updated_at")
    srb.addField("created_by")

    //srb.addField("*")
    srb.setFrom((page.page - 1) * page.size).setSize(page.size)
  }

  def setHighlightToSearchRequestBuilder(srb: SearchRequestBuilder) = {
    srb.addHighlightedField("title")
    srb.addHighlightedField("file_content")
    srb.setHighlighterPreTags("<mark>")
    srb.setHighlighterPostTags("</mark>")
  }

  def setQueryToSearchRequestBuilder(srb: SearchRequestBuilder, createdBy: Int,
                                     keywordList: List[String], includeExtList: List[String]) = {
    val keyword = keywordList.mkString(" ")
    logger.warn("keyword : " + keyword)

    //    val query = QueryBuilders.filteredQuery(
    //      this.getQueryBuilder(keyword), this.getFilterBuilder(createdBy, keyword, includeExtList))
    //    srb.setQuery(query)

    srb.setPostFilter(this.getFilterBuilder(createdBy, keyword, includeExtList))
    srb.setQuery(this.getQueryBuilder(keyword))
  }

  def setSortToSearchRequestBuilder(srb: SearchRequestBuilder) = {
    srb.addSort(SortBuilders.scoreSort())
  }

  def getQueryBuilder(keyword: String): QueryBuilder = {
    if (keyword == "") {
      QueryBuilders.matchAllQuery()
    }
    else {
      QueryBuilders.multiMatchQuery(keyword, "file_content", "file_path")
        //QueryBuilders.multiMatchQuery(keyword, "file_content")
        .operator(Operator.AND)
    }
  }

  def getFilterBuilder(createdBy: Int, keyword: String, includeExtList: List[String])
  : FilterBuilder = {
    val and = FilterBuilders.andFilter()

    and.add(FilterBuilders.termFilter("created_by", createdBy))

    if (keyword != "") {
      val keywordFilter = FilterBuilders.orFilter()

      keywordFilter.add(FilterBuilders.queryFilter(
        QueryBuilders.matchQuery("file_path", keyword).operator(Operator.AND)))
      keywordFilter.add(FilterBuilders.queryFilter(
        QueryBuilders.matchQuery("file_content", keyword).operator(Operator.AND)))

      and.add(keywordFilter)
    }

    if (!includeExtList.isEmpty) {
      logger.warn("use ext filter")
      val fileExtFilter = {
        val fb = FilterBuilders.orFilter()
        includeExtList.foreach(x => {
          fb.add(FilterBuilders.termFilter("file_ext", x))
        })
        fb
      }

      and.add(fileExtFilter)
    }

    return and
  }

  def getFilePathList
  (
    esAddress: String, esPort: Int, esUrl: String,
    indexName: IndexName, mappingName: MappingName,
    createdBy: Int, page: Page
  ): List[String] = {
    val client = new TransportClient()
    client.addTransportAddress(new InetSocketTransportAddress(esAddress, esPort))

    val srb = client.prepareSearch(indexName.value.value)
    srb.setTypes(mappingName.value.value)
    srb.addField("file_path").setFrom((page.page - 1) * page.size).setSize(page.size)

    val query = QueryBuilders.filteredQuery(
      QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("created_by", createdBy))

    srb.setQuery(query)
    val response = srb.execute().actionGet()
    response.getHits().getHits().map(x => x.field("file_path").getValue[String]()).toList
  }


}

/*
{
  "fields" : ["title", "file_path", "created_at", "updated_at", "created_by"],
  "query": {
    "multi_match" : {
      "query" : "install",
      "fields" : ["file_content", "file_path"],
      "operator" : "and"
    }
  },
  "highlight" : {
        "pre_tags" : ["<em>"],
        "post_tags" : ["</em>"],
        "fields" : {
            "title" : {},
            "file_content" : {}
        }
  },
  "filter" : {
    "and" : [
      {
        "or" : [
          {
            "query" : {
              "match" : {
                "file_path" : {
                  "query" : "install install",
                  "operator" : "and"
                }
              }
            }
          },
          {
            "query" : {
              "match" : {
                "file_content" : {
                  "query" : "install install",
                  "operator" : "and"
                }
              }
            }
          }
        ]
      },
      {
        "term" : {
          "created_by" : 2
        }
      }
    ]
  },
  "sort" : [
    {
      "_score" : "desc"
    }
  ],
  "from" : 0,
  "size" : 10,
  "version" : true,

}


*/