package com.example.hafizgoo.controller;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @Auther: hafizgoo
 * @Date: DATE−2022/7/25 - MONTH−07 - DAY−25 - TIME−13:34
 * @Description: com.example.hafizgoo.controller
 * @version: 1.0
 */

@RestController
public class KafkaController {

    private final static String TOPIC_NAME = "replicated-test";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/msg")
    public void sendMsg() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 1000; i++) {


            /*
             * 异步发送
             */
            ListenableFuture<SendResult<String, String>> result = kafkaTemplate.send(TOPIC_NAME, "***" + i);
            result.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onSuccess(SendResult<String, String> stringStringSendResult) {
                    printlnRest(stringStringSendResult);
                }
            });
        }
    }

    public void printlnRest(SendResult<String, String> stringStringSendResult) {
        ProducerRecord<String, String> producerRecord = stringStringSendResult.getProducerRecord();
        RecordMetadata recordMetadata = stringStringSendResult.getRecordMetadata();
        StringBuilder sb = new StringBuilder();
        sb.append("msg发送到了topic:");
        sb.append(producerRecord.topic());
        sb.append("  partition: ");
        sb.append(recordMetadata.partition());
        sb.append("  offset: ");
        sb.append(recordMetadata.offset());
        System.out.println(sb.toString());
    }


}

