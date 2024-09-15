package com.xeticaret.bigdata.autocomplete.api.service;

import com.google.gson.Gson;
import com.xeticaret.bigdata.autocomplete.api.interfaces.IAutocompleteService;
import com.xeticaret.bigdata.autocomplete.api.model.AutoCompleteDetail;
import com.xeticaret.bigdata.autocomplete.api.model.AutocompleteResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AutocompleteService implements IAutocompleteService {

    private RestHighLevelClient client;
    private final Gson gson = new Gson();
    private SearchSourceBuilder builder;

    @Value("${elasticsearch.host}")
    private String ip;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.index}")
    private String index;

    @Value("${elasticsearch.field}")
    private String field;

    @PostConstruct
    public void init() {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(ip, port)));
        builder = new SearchSourceBuilder();
    }

    @Override
    public AutocompleteResponse search(String term) {
        List<AutoCompleteDetail> data = new ArrayList<>();
        builder.query(QueryBuilders.matchQuery(field, term));
        SearchRequest req = new SearchRequest(index).source(builder);

        try {
            SearchResponse res = client.search(req, RequestOptions.DEFAULT);
            for (SearchHit hit : res.getHits().getHits()) {
                AutoCompleteDetail autoCompleteDetail = gson.fromJson(hit.getSourceAsString(), AutoCompleteDetail.class);
                data.add(autoCompleteDetail);
            }
            return new AutocompleteResponse(data);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during the search operation", e);
        }
    }
}
