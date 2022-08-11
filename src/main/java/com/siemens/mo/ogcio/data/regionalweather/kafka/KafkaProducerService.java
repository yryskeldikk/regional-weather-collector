package com.siemens.mo.ogcio.data.regionalweather.kafka;

import com.siemens.mo.ogcio.data.regionalweather.kafka.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Service
public class KafkaProducerService {

    Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    KafkaTemplate kafkaTemplate;

    private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

    private Thread consumer = new Thread(this::consume);


    @PostConstruct
    private void onInit() {
        consumer.start();
    }

    public void consume() {
        while (true) {
            Message message;
            try {
                message = messageQueue.take();
            } catch (InterruptedException e) {
                break;
            }
            kafkaTemplate.send(message.getTopic(), message.getMessage());
            logger.info("KafkaProducerService: Sent message=[" + message.getMessage() +
                    "] to topic=[" + message.getTopic() + "]");
        }
    }

    public void addToMessageQueue(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            return;
        }
    }

}
