package org.relaxcoder.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
    String topicName = "test-topic";
    KafkaProducer<String, String> kafkaProducer;

    public MessageProducer( Map<String, Object> propMap) {
        this.kafkaProducer = new KafkaProducer<>(propMap);
    }

    Callback callback = (recordMetadata, exception) -> {
      if(exception != null){
          logger.error("Exception is {}", exception.getMessage());
      }
      else{
          logger.info("Record Metadata Async in Callback Offset: {}  and the partition is {} ",
                  recordMetadata.offset(),
                  recordMetadata.partition()
          );
      }
    };

    public void close(){
        kafkaProducer.close();
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
            logSuccessResponse(message, key, recordMetadata);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Exception in publishMessageSync: {}", e.getMessage());
        }
    }

    public void publishMessageAsync(String key, String message){
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, key, message);
        kafkaProducer.send(producerRecord, callback);
    }
    public void logSuccessResponse(String message, String key, RecordMetadata recordMetadata){
        logger.info("Message **{}** sent successfully with the key **{}** .", message, key);
        logger.info("Published Record Offset is {} and the partition is {}",
                recordMetadata.offset(), recordMetadata.partition());
    }
    public static void main(String[] args) throws InterruptedException {
        Map<String, Object> producerProps = propsMap();
        MessageProducer messageProducer = new MessageProducer(producerProps);
        messageProducer.publishMessageAsync("99", "Him");
        messageProducer.publishMessageAsync("99", "Pan");
        Thread.sleep(5000);
    }
}
