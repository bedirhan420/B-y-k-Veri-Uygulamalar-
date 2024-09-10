package com.bigdatacompany.eticaret.api;

import com.bigdatacompany.eticaret.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class SearchController {

    private static final String API_KEY = System.getenv("API_KEY");
    private static final String API_URL = System.getenv("API_URL");
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    MessageProducer messageProducer;

    private static final String[] SAMPLE_TERMS = {
            "laptop", "smartphone", "tablet", "headphones", "keyboard",
            "mouse", "monitor", "printer", "webcam", "charger",
            "camera", "smartwatch", "speaker", "external hard drive", "USB drive",
            "microphone", "router", "modem", "dock station", "power bank",
            "graphics card", "motherboard", "RAM", "processor", "SSD",
            "HDD", "cooling fan", "case", "power supply", "bluetooth headset",
            "VR headset", "gaming chair", "gaming keyboard", "gaming mouse"
    };

    private static final String[] SAMPLE_CITIES = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix"};

    // Fixed array of user_id values
    private static final int[] USER_IDS = {1010, 1020, 1030, 1040, 1050, 1060, 1070, 1080, 1090, 1100,
            1110, 1120, 1130, 1140, 1150, 1160, 1170, 1180, 1190, 1200};

    private static final long TIMESTAMP_START = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30; // 30 days ago
    private static final long TIMESTAMP_END = System.currentTimeMillis();

    @GetMapping("/search")
    public JSONObject searchIndex(@RequestParam String term, @RequestParam String ipAddress) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String cityName = getCityFromIp(ipAddress);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("search", term);
        jsonObject.put("timestamp", timestamp.toString());
        jsonObject.put("region", cityName);
        messageProducer.send(jsonObject.toJSONString());
        logger.info("Search request: " + jsonObject.toJSONString());

        return jsonObject;
    }

    @GetMapping("/search/stream")
    public JSONObject searchIndexStream(@RequestParam String term, @RequestParam String ipAddress) {
        Random random = new Random();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String cityName = getCityFromIp(ipAddress);
        int userId = USER_IDS[random.nextInt(USER_IDS.length)];

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("search", term);
        jsonObject.put("current_ts", timestamp.toString());
        jsonObject.put("region", cityName);
        jsonObject.put("user_id", userId);
        messageProducer.send(jsonObject.toJSONString());
        logger.info("Search request: " + jsonObject.toJSONString());

        return jsonObject;
    }

    @GetMapping("/generateRandomData")
    public List<JSONObject> generateRandomData(@RequestParam int count) {
        List<JSONObject> randomDataList = new ArrayList<>();
        Random random = new Random();

        // Validate the count parameter
        if (count <= 0) {
            logger.error("Invalid count parameter: " + count);
            throw new IllegalArgumentException("Count must be a positive integer");
        }

        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = new JSONObject();
            String term = SAMPLE_TERMS[random.nextInt(SAMPLE_TERMS.length)];
            String cityName = SAMPLE_CITIES[random.nextInt(SAMPLE_CITIES.length)];
            Timestamp timestamp = new Timestamp(TIMESTAMP_START + (long) (random.nextDouble() * (TIMESTAMP_END - TIMESTAMP_START)));

            // Randomly pick a user_id from the fixed pool of 20 user_ids
            int userId = USER_IDS[random.nextInt(USER_IDS.length)];

            jsonObject.put("search", term);
            jsonObject.put("timestamp", timestamp.toString());
            jsonObject.put("region", cityName);
            jsonObject.put("user_id", userId);  // Using random user_id from the predefined array

            randomDataList.add(jsonObject);
            messageProducer.send(jsonObject.toJSONString());
        }

        return randomDataList;
    }



    private String getCityFromIp(String ipAddress) {
        String url = API_URL + ipAddress + "?access_key=" + API_KEY;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return extractCityFromResponse(response);
    }

    private String extractCityFromResponse(String response) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(response);
            String city = (String) json.get("city");
            return city != null ? city : "Bilinmeyen Şehir";
        } catch (ParseException e) {
            logger.error("Error parsing city from IP response: " + e.getMessage());
            return "Bilinmeyen Şehir";
        }
    }
}

