package org.relaxcoder.consumers;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageConsumerSynchronousCommitSpecificOffset {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerSynchronousCommitSpecificOffset.class);
    private final Map<TopicPartition, OffsetAndMetadata> offsetsMap;
    KafkaConsumer<String, String> kafkaConsumer;
    String topicName = "test-topic-replicated";

    public MessageConsumerSynchronousCommitSpecificOffset(Map<String, Object> propsMap){
        kafkaConsumer = new KafkaConsumer<>(propsMap);
        offsetsMap = new HashMap<>();
    }

    public static Map<String, Object> buildConsumerProperties(){
        Map<String, Object> propMap = new HashMap<>();
        propMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092, localhost:9093, localhost:9094");
        propMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propMap.put(ConsumerConfig.GROUP_ID_CONFIG, "messageConsumer1");
        propMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //propMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return propMap;
    }

    public void pollKafka(){

        kafkaConsumer.subscribe(List.of(topicName));
        Duration timeOutDuration = Duration.of(100, ChronoUnit.MILLIS);

        try {
            while(true){
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(timeOutDuration);
                consumerRecords.forEach(
                        record -> {
                            logger.info("Consumer Record key is {} and the value is {} and the partition is {}",
                                    record.key(),
                                    record.value(),
                                    record.partition()
                            );

                            offsetsMap.put(
                                    new TopicPartition(record.topic(), record.partition()),
                                    new OffsetAndMetadata(record.offset() + 1, null)
                            );
                        }
                );

                if(consumerRecords.count() > 0){
                    kafkaConsumer.commitSync(offsetsMap);
                    logger.info("Message Committed");
                }
            }
        }
        catch(CommitFailedException e){
            logger.error("CommitFailedException in poll kafka : " + e);
        }
        catch (Exception e){
            logger.error("Exception in pollKafka : " + e);
        }
        finally {
            kafkaConsumer.close();
        }
    }

    public static void main(String[] args) {
        Map<String, Object> propsMap = buildConsumerProperties();
        MessageConsumerSynchronousCommitSpecificOffset messageConsumer = new MessageConsumerSynchronousCommitSpecificOffset(propsMap);
        messageConsumer.pollKafka();
    }
}
