package com.erving;

import com.alibaba.fastjson.JSON;
import com.erving.dto.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;

@SpringBootTest
class ElasticsearchApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void testCreateIndex() throws IOException {
        //1. 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("erving_index");
        //2. 执行请求
        CreateIndexResponse response = restHighLevelClient.indices()
                                                .create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void testIndexExist() throws IOException {
        GetIndexRequest request = new GetIndexRequest("erving_index");
        boolean res = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(res);
    }

    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("erving_index");
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }

    //添加文档
    @Test
    void testIndexDocument() throws IOException {
        IndexRequest request = new IndexRequest("post").id("1")
                .source("user", "erving",
                        "postDate", new Date(),
                        "message", "test index document");
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }
    @Test
    void testAddDocument() throws IOException {
        IndexRequest post_idnex = new IndexRequest("post");
        for(int i=0; i<10; i++){
            User user = new User("erving"+ i, "" + i*10, "test add document for the" +i +"times");
            post_idnex.id(Integer.toString(i)).source(JSON.toJSONString(user), XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(post_idnex, RequestOptions.DEFAULT);
            System.out.println(response.toString());
        }
    }

    @Test
    void testGetDocument() throws IOException {
        GetRequest post_index = new GetRequest("post");
        post_index.id("2");
        GetResponse response = restHighLevelClient.get(post_index, RequestOptions.DEFAULT);
        System.out.println(response.toString());


        post_index.id("3");
        post_index.fetchSourceContext(new FetchSourceContext(false));
        post_index.storedFields("_none_");
        response = restHighLevelClient.get(post_index, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }


}
