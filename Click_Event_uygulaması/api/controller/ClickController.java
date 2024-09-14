package com.xcommerce.bigdata.clickevents.api.controller;

import com.xcommerce.bigdata.clickevents.api.model.ClickRequest;
import com.xcommerce.bigdata.clickevents.api.service.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClickController {

    @Autowired
    ClickEventService clickEventService;

    @Autowired
    TimestampService timestampService;

    @Autowired
    IPRegionService ipRegionService;

    @Autowired
    RandomDataGenerator randomDataGenerator;

    @PostMapping("/click")
    public JSONObject clickEvent(@RequestBody ClickRequest req, @RequestHeader("X-Forwarded-For") String ipAddress) {
        String deviceID = req.getDeviceId();
        String clickedButton = req.getClickItem();
        String session = req.getSession();

        String timestamp = timestampService.getCurrentTimestamp();
        req.setCurrent_ts(timestamp);

        String region = ipRegionService.getRegionByIp(ipAddress);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceID", deviceID);
        jsonObject.put("clickedButton", clickedButton);
        jsonObject.put("session", session);
        jsonObject.put("currentTs", timestamp);
        jsonObject.put("region", region);

        return clickEventService.handleClickEvent(jsonObject);
    }

    @PostMapping("/generate-random-clicks/{count}")
    public JSONArray generateRandomClickEvents(@PathVariable int count) {
        JSONArray clickEvents = new JSONArray();

        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = new JSONObject();
            String deviceID = randomDataGenerator.generateDeviceId();
            String clickedButton = randomDataGenerator.generateClickButton();
            String session = randomDataGenerator.generateSession();
            String timestamp = randomDataGenerator.generateTimestamp();
            String region = randomDataGenerator.generateRegion();

            jsonObject.put("deviceID", deviceID);
            jsonObject.put("clickedButton", clickedButton);
            jsonObject.put("session", session);
            jsonObject.put("currentTs", timestamp);
            jsonObject.put("region", region);

            JSONObject resultJSONObject = clickEventService.handleClickEvent(jsonObject);

            clickEvents.add(resultJSONObject);
        }

        return clickEvents;
    }
}
