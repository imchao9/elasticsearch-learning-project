package com.chao.project.config;

import com.chao.project.biz.common.constant.StringPoolConstant;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * ElasticSearch配置类
 * @author zhangjunchao
 */
@Configuration
public class ElasticsearchClientConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchClientConfiguration.class);

    public static int CONNECT_TIMEOUT_MILLIS = 1000;

    public static int SOCKET_TIMEOUT_MILLIS = 30000;

    public static int CONNECTION_REQUEST_TIMEOUT_MILLIS = 500;

    public static int MAX_CONN_PER_ROUTE = 10;

    public static int MAX_CONN_TOTAL = 30;

    @Value("${elasticsearch.clusterNodes}")
    private String clusterNodes;

    private RestHighLevelClient restHighLevelClient;

    private HttpHost[] loadHttpHosts() {
        String[] clusterNodesArray = clusterNodes.split(StringPoolConstant.COMMA);
        HttpHost[] httpHosts = new HttpHost[clusterNodesArray.length];


        for(Integer i =0;i<clusterNodesArray.length;i++){
            String clusterNode = clusterNodesArray[i];
            String[] hostAndPort = clusterNode.split(StringPoolConstant.COLON);
            httpHosts[i] = new HttpHost(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
        }
        return httpHosts;
    }

    @Bean
    public RestHighLevelClient restClient() {
        RestClientBuilder restClientBuilder = RestClient.builder(loadHttpHosts());

        // 设置链接超时时间等参数
        setConnectTimeOutConfig(restClientBuilder);
        setConnectConfig(restClientBuilder);

        

        restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        return restHighLevelClient;
    }

    private void setConnectConfig(RestClientBuilder restClientBuilder) {
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            requestConfigBuilder.setSocketTimeout(SOCKET_TIMEOUT_MILLIS);
            requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MILLIS);
            return requestConfigBuilder;
        });
    }

    private void setConnectTimeOutConfig(RestClientBuilder restClientBuilder) {
        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(MAX_CONN_TOTAL);
            httpClientBuilder.setMaxConnPerRoute(MAX_CONN_PER_ROUTE);
            return httpClientBuilder;
        });
    }

    @PreDestroy
    public void close() {
        if (restHighLevelClient != null) {
            try {
                LOGGER.info("Closing the ES REST client");
                restHighLevelClient.close();
            } catch (IOException e) {
                LOGGER.error("Problem occurred when closing the ES REST client", e);
            }
        }
    }


}
