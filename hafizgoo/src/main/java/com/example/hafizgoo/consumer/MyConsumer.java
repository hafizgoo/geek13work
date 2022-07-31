package com.example.hafizgoo.consumer;

/**
 * @Auther: hafizgoo
 * @Date: DATE−2022/7/25 - MONTH−07 - DAY−25 - TIME−13:45
 * @Description: com.example.hafizgoo.consumer
 * @version: 1.0
 */


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.util.Map;

@Component
public class MyConsumer {

    private final static String TOPIC_NAME = "replicated-test";

    @KafkaListener(topics = TOPIC_NAME)
    public void consumerMsg(ConsumerRecord<String, String> record, Consumer consumer) {
        printlnRecord(record);

        /**
         * 异步提交
         */
        consumer.commitAsync(new OffsetCommitCallback() {
            @Override
            public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                }else {
                    System.out.println(offsets.toString());
                }
            }
        });
    }

    public void printlnRecord(ConsumerRecord<String, String> record) {
        StringBuilder sb = new StringBuilder();
        sb.append("接收到topic为：");
        sb.append(record.topic());
        sb.append("  分区为：");
        sb.append(record.partition());
        sb.append("  offset为：");
        sb.append(record.offset());
        System.out.println(sb.toString());
    }
}

