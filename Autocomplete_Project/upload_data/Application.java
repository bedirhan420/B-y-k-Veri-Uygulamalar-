package com.xeticaret.bigdata.autocomplete.upload_data;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String ip = dotenv.get("IP");
        int port = Integer.parseInt(dotenv.get("ES_PORT"));

        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(ip,port)));
        IndexRequest req = new IndexRequest("autocomplete");

        String path = "C:\\Users\\bedir\\IdeaProjects\\autocomplate_project\\src\\main\\java\\com\\xeticaret\\bigdata\\autocomplete\\upload_data\\phone_brands_models.csv";
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNext()){
            String line = scanner.nextLine();
            String[] terms = line.split(",");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("brand",terms[0]);
            jsonObject.put("title",terms[1]);

            req.source(jsonObject.toJSONString(), XContentType.JSON);
            client.index(req, RequestOptions.DEFAULT);
        }

    }
}
