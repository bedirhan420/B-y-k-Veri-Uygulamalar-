package com.xcommerce.bigdata.clickevents.api.service;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class TimestampService {

    public String getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis()).toString();
    }
}
