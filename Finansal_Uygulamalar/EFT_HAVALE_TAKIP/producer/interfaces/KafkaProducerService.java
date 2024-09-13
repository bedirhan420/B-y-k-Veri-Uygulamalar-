package com.xbank.bigdata.efthavale.producer.interfaces;

public interface KafkaProducerService {
    void Send(String topic,String data);
    void Close();
}
