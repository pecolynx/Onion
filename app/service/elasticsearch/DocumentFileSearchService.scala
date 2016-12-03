package service.elasticsearch

import com.kujilabo.service.RestClient
import com.typesafe.scalalogging.LazyLogging
import models.core.Page
import models.elasticsearch.{EsSearchResponse, _}
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.index.query.MatchQueryBuilder.Operator
import org.elasticsearch.index.query._
import org.elasticsearch.search.sort.SortBuilders
import service.elasticsearch.es.EsMappingService
import utils.JsonUtils

object DocumentFileSearchService extends LazyLogging {

  def search(esUrl: String,
             indexName: IndexName, mappingName: MappingName, createdBy: Int,
             searchCondition: FileSearchCondition): SearchResult = {
    logger.warn("keyword = " + searchCondition.keywordList.mkString(" "))
    val request = JsonUtils.toJson(DocumentFileSearchRequest.getMap(createdBy,
      searchCondition.keywordList.mkString(" "),
      searchCondition.page.size * (searchCondition.page.page - 1), searchCondition.page.size))
    val url = esUrl + "/" + indexName + "/_search?pretty"
    val response: String = RestClient.post(url, request).getBody
    val esSearchResponse = JsonUtils.toObject(response, classOf[EsSearchResponse])


    logger.debug("request : " + request)
    logger.debug("took : " + esSearchResponse.took)
    logger.warn("response : " + response)
    logger.debug("response length : " + response.toString().length())

    val fileMapping = EsMappingService.getMapping(esUrl, indexName, mappingName)
    def getMapping(indexName: IndexName, mappingName: MappingName) = {
      fileMapping
    }

    return new SearchResult(new EsDocumentBuilder().buildHitList(esSearchResponse, getMapping),
      esSearchResponse.getHits.getTotalHits())
    //return null
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

    logger.warn("createdBy : " + createdBy)
    val query = QueryBuilders.filteredQuery(
      QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("created_by", createdBy))

    srb.setQuery(query)
    val response = srb.execute().actionGet()
    logger.warn("file path length = " + response.getHits().getTotalHits)
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