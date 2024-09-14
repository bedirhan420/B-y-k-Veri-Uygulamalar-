package com.xcommerce.bigdata.clickevents.api.service;

import com.xcommerce.bigdata.clickevents.api.interfaces.IEventProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventProducer implements IEventProducer {

    private final Producer<String, String> producer;

    @Autowired
    public KafkaEventProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void send(String data) {
        ProducerRecord<String, String> rec = new ProducerRecord<>("clickevent", data);
        producer.send(rec);
    }

    public void close() {
        producer.close();
    }
}
