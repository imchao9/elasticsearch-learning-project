package com.chao.project;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;

@SpringBootTest(classes = ElasticSearchLearningProject.class)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class RestClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientTest.class);

    @Autowired
    private RestHighLevelClient restClient;

    /**
     * 测试用index名称
     */
    private final static String TEST_INDEX = "test_index";

    @Before
    public void init() throws IOException {
        this.deleteIndex(TEST_INDEX);
        this.createIndex(TEST_INDEX);
    }

    @Test
    public void testQuery() throws IOException {
        Data data = new Data("2", "测试数据02");
        this.insertData(data);
        this.queryById(data);
    }

    private void createIndex(String index) throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 5)
                .put("index.number_of_replicas", 1));

        createIndexRequest.mapping("{\n"
                + "            \"properties\":{\n"
                + "                \"name\":{\n"
                + "                    \"type\":\"text\"\n"
                + "                }\n"
                + "            }\n"
                + "        }", XContentType.JSON);

        CreateIndexResponse createIndexResponse = restClient.indices().create(createIndexRequest,
                RequestOptions.DEFAULT);
        LOGGER.info("create index {} response:{}", index, createIndexResponse.isAcknowledged());
    }

    private void deleteIndex(String index) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = restClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            LOGGER.info("delete index {} response:{}", index, response.isAcknowledged());
        } catch (IOException e) {
            LOGGER.info("delete index exception", e);
        }
    }

    private void testInsert() throws IOException {
        Data data = new Data("1", "测试数据01");
        this.insertData(data);
    }

    private void insertData(Data data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(TEST_INDEX);
        indexRequest.id(data.getId());

        indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        indexRequest.source(JSONObject.toJSONString(data), XContentType.JSON);
        IndexResponse indexResponse = restClient.index(indexRequest, RequestOptions.DEFAULT);
        Assert.assertEquals(indexResponse.status(), RestStatus.CREATED);
    }

    private void queryById(Data data) throws IOException {
        SearchRequest searchRequest = new SearchRequest(TEST_INDEX);
        searchRequest.source(new SearchSourceBuilder().query(
                QueryBuilders.termQuery("id" , data.getId()))
        );
        SearchResponse searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
        Assert.assertEquals(searchResponse.status(), RestStatus.OK);

        // 查询条数
        SearchHits hits = searchResponse.getHits();
        Assert.assertEquals(1, hits.getTotalHits().value);

        // 判断查询数据与插入数据是否相等
        String dataJson = hits.getHits()[0].getSourceAsString();
        Assert.assertEquals(JSON.toJSONString(data), dataJson);
    }

    @Test
    public void testUpdate() throws IOException {
        // 插入数据
        Data data = new Data("3", "测试数据03");
        this.insertData(data);

        // 更新数据
        data.setName("测试数据被更新");
        UpdateRequest updateRequest = new UpdateRequest(TEST_INDEX, data.getId());
        updateRequest.doc(JSONObject.toJSONString(data), XContentType.JSON);

        // 强制刷新数据
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        UpdateResponse updateResponse = restClient.update(updateRequest, RequestOptions.DEFAULT);

        // 查询更新结果
        this.queryById(data);
    }

    @Test
    public void testDelete() throws IOException {
        Data data = new Data("4", "测试数据04");
        this.insertData(data);

        // 删除数据
        DeleteRequest deleteRequest = new DeleteRequest(TEST_INDEX, data.getId());
        // 强制刷新
        deleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        DeleteResponse deleteResponse = restClient.delete(deleteRequest, RequestOptions.DEFAULT);
        Assert.assertEquals(deleteResponse.status(), RestStatus.OK);

        // 查询数据
        SearchRequest searchRequest = new SearchRequest(TEST_INDEX);
        searchRequest.source(new SearchSourceBuilder().query(
                QueryBuilders.termQuery("id", data.getId()))
        );
        SearchResponse searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
        Assert.assertEquals(searchResponse.status(), RestStatus.OK);

        // 查询条数为1
        SearchHits hits = searchResponse.getHits();
        Assert.assertEquals(0, hits.getTotalHits().value);

    }


}
