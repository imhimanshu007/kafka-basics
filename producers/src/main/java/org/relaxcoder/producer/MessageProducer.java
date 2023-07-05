package org.relaxcoder.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MessageProducer {
    String topicName = "test-topic";
    KafkaProducer<String, String> kafkaProducer;

    public MessageProducer( Map<String, Object> propMap) {
        this.kafkaProducer = new KafkaProducer<>(propMap);
    }

    public static Map<String, Object> propsMap(){
        Map<String, Object> propMap = new HashMap<>();
        propMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092, localhost:9093, localhost:9094");
        propMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return propMap;
    }

    public void publishMessageSync(String key, String message){
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, key, message);
        RecordMetadata recordMetadata = null;
        try {
            recordMetadata = kafkaProducer.send(producerRecord).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        Map<String, Object> producerProps = propsMap();
        MessageProducer messageProducer = new MessageProducer(producerProps);
        messageProducer.publishMessageSync(null, "ABC");
        messageProducer.publishMessageSync(null, "DEF");
    }
}
