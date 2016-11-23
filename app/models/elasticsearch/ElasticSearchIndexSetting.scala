package models.elasticsearch

import org.elasticsearch.common.xcontent.XContentFactory

class ElasticSearchIndexSetting {
  def get(): String = {

    val mappingBuilder = XContentFactory.jsonBuilder()
      .startObject()
      .startObject("settings")
      .startObject("index")
      .startObject("analysis")
      .startObject("filter")
      .startObject("pos_filter")
      .field("type", "kuromoji_part_of_speech")
      .field("stoptags", "助詞-格助詞-一般", "助詞-終助詞")
      .endObject()
      .startObject("greek_lowercase_filter")
      .field("type", "lowercase")
      .field("language", "greek")
      .endObject()
      .endObject()
      .startObject("analyzer")
      .startObject("kuromoji_analyzer")
      .field("type", "custom")
      .field("tokenizer", "kuromoji_tokenizer")
      .field("filter", "kuromoji_baseform", "pos_filter", "greek_lowercase_filter", "cjk_width")
      .endObject()
      .endObject()
      .endObject()
      .endObject()
      .endObject()
      .endObject()

    return mappingBuilder.string()
  }
}
