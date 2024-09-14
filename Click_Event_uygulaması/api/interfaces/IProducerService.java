package com.xcommerce.bigdata.clickevents.api.interfaces;

import com.xcommerce.bigdata.clickevents.api.model.ClickRequest;
import org.json.simple.JSONObject;

public interface IProducerService {
    JSONObject producer(JSONObject req);
}
