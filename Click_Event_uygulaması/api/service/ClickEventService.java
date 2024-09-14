package com.xcommerce.bigdata.clickevents.api.service;

import com.xcommerce.bigdata.clickevents.api.interfaces.IProducerService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClickEventService {

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private IProducerService kafkaProducerService;

    public JSONObject handleClickEvent(JSONObject jsonObject) {
        // Log the click event
        loggingService.logInfo("Click event: " + jsonObject.toJSONString());

        // Send the event to Kafka and update the JSON object with the producer status
        JSONObject producerStatus = kafkaProducerService.producer(jsonObject);
        jsonObject.put("producerStatus", producerStatus);

        return jsonObject;
    }
}
