package com.xcommerce.bigdata.clickevents.api.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.simple.JSONObject;

@Service
public class IPRegionService {
    private  static Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String API_URL = dotenv.get("API_URL");

    public String getRegionByIp(String ipAddress) {
        try {
            String url = API_URL + ipAddress + "/json/?key=" + API_KEY;
            RestTemplate restTemplate = new RestTemplate();
            JSONObject response = restTemplate.getForObject(url, JSONObject.class);
            if (response != null) {
                return (String) response.get("region");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Region";
    }
}
