package com.xcommerce.bigdata.clickevents.consumer.client;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;

public class ElasticSearchClient {
    private final RestHighLevelClient client;
    private final String index;

    public ElasticSearchClient() {
        Dotenv dotenv = Dotenv.load();
        String ip = dotenv.get("IP");
        this.client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ip, 9200, "http")));
        this.index = "clickevent";
    }

    public void indexDocument(ConsumerRecord<String, String> record) {
        IndexRequest request = new IndexRequest(index);
        request.source(record.value(), XContentType.JSON);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
