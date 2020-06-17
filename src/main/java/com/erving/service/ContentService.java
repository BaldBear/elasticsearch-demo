package com.erving.service;

import com.alibaba.fastjson.JSON;
import com.erving.dto.JdContent;
import com.erving.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author erving
 * @date 2020/6/15
 **/
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    HtmlParseUtil htmlParseUtil;

    public boolean parseContent(String keyword){
        try {
            List<JdContent> contents = htmlParseUtil.parseJD(keyword);

            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.timeout("3m");

            for(int i=0; i<contents.size(); i++){
                bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
            }

            BulkResponse responses = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            return !responses.hasFailures();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize){
        if(pageNo <=1){
            pageNo = 1;
        }

        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(2, TimeUnit.MINUTES));

        //搜索高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<Map<String, Object>> list = new ArrayList<>();
        assert response != null;
        for(SearchHit hit: response.getHits().getHits()){

            System.out.println(hit.toString());
            System.out.println("==================================================");
            //解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> map = hit.getSourceAsMap();
            if(null != title){
                Text[] fragments = title.fragments();
                System.out.println(fragments[0].toString());
                String name = ""+fragments[0];
                map.put("title", name);
            }
            list.add(map);
        }

        return list;
    }
}
