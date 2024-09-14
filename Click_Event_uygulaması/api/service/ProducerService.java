package com.xcommerce.bigdata.clickevents.api.service;

import com.google.gson.Gson;
import com.xcommerce.bigdata.clickevents.api.interfaces.IEventProducer;
import com.xcommerce.bigdata.clickevents.api.interfaces.IProducerService;
import com.xcommerce.bigdata.clickevents.api.model.ClickRequest;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ProducerService implements IProducerService {

    private final IEventProducer eventProducer;
    private Gson gson;
    private JSONObject jsonObject;

    @Autowired
    public ProducerService(IEventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @PostConstruct
    public void init() {
        gson = new Gson();
        jsonObject = new JSONObject();
    }

    @Override
    public JSONObject producer(JSONObject req) {
        String jsonData = gson.toJson(req);
        JSONObject response = new JSONObject();
        try {
            eventProducer.send(jsonData);
            response.put("producerStatus", "success");
        } catch (Exception e) {
            response.put("producerStatus", "fail");
        }
        return response;
    }

}
