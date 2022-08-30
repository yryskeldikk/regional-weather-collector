package com.siemens.mo.ogcio.data.regionalweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.mo.ogcio.data.regionalweather.bean.Wind;
import com.siemens.mo.ogcio.data.regionalweather.exception.ApiRetrievalException;
import com.siemens.mo.ogcio.data.regionalweather.kafka.KafkaProducerService;
import com.siemens.mo.ogcio.data.regionalweather.kafka.message.Message;
import com.siemens.mo.ogcio.data.regionalweather.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "app", name = "collector.active", havingValue = "wind")
public class WindCollector {
    private final Logger logger = LoggerFactory.getLogger(AirTemperatureCollector.class);

    @Value("${app.api.regionalweather.wind.url}")
    private String windUrl;

    @Value("${spring.cloud.stream.kafka.binder.topic.wind}")
    private String windKafkaTopic;

    @Value("${app.api.regionalweather.retry.count}")
    private int retryCount;

    @Value("${app.api.regionalweather.retry.delay}")
    private int retryDelay;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Resource
    RestTemplate restTemplate;

    @Scheduled(cron = "${app.api.regionalweather.wind.cron}")
    public void scheduledCollect() throws Exception {
        collect();
    }

    public void collect() {
        int successCount = 0;

        Map.Entry<Void,Throwable> stopExecution = retryAble( () -> {
            List<Wind> data = collectWindData();
            if( isEmpty(data) ){
                throw new Exception("WindCollector: data is still empty after retrieve process" );
            }
            return null;
        }, retryCount, retryDelay );
        if( stopExecution.getValue() != null ){
            logger.error(
                    "WindCollector: Encountered error while collecting data after retrying " + retryCount + " times with delay " + retryDelay + "ms",
                    stopExecution.getValue()
            );
        } else {
            successCount++;
        }

        logger.info("WindCollector: completed collecting data successfully ...");
    }

    public static <T> Map.Entry<T,Throwable> retryAble(ThrowableSupplier<T> supplier, int retryCount, long delay_ms ){
        T result = null;
        Throwable t = null;
        try{
            result = supplier.get();
        } catch ( Throwable e ){
            // attempt to retry again
            if( retryCount > 0 ){
                // make sure there's a delay first before retry
                try {
                    Thread.sleep( delay_ms );
                } catch (InterruptedException ex) {
                    // we will ignore this interrupt because ultimately just want to retry / fail
                }
                return retryAble( supplier, retryCount - 1, delay_ms * 2 );
            } else {
                t = e;
            }
        }
        return new AbstractMap.SimpleEntry<>( result, t );
    }

    @FunctionalInterface
    public interface ThrowableSupplier<T> {
        T get() throws Throwable;
    }

    public List<Wind> collectWindData() throws JsonProcessingException, ApiRetrievalException, ParseException {
        logger.info("WindCollector: start retrieving data from API ...");

        ResponseEntity<String> response = restTemplate.getForEntity(windUrl, String.class);
        if (response.getStatusCode() != HttpStatus.OK)
            throw new ApiRetrievalException(response.getStatusCode());

        String[] data = response.getBody().split("\\r?\\n|\\r");

        List<Wind> result = new ArrayList<>();

        // start from 1 skipping the header
        for (int i = 1; i < data.length; i++) {
            String[] line = data[i].split(",");
            Wind wind = new Wind();
            wind.setTimestamp(DateUtil.toUTC(line[0]));
            wind.setAutomaticWeatherStation(line[1]);
            wind.setMeanWindDirection(line[2].equals("N/A") ? null : line[2]);
            wind.setMeanSpeed(line[3].equals("N/A") ? null : Integer.parseInt(line[3]));
            wind.setMaxGust(line[4].equals("N/A") ? null : Integer.parseInt(line[4]));
            result.add(wind);
        }

        logger.info("WindCollector: retrieved data of size {} ...", result.size());

        publishToKafka(result);

        return result;
    }

    public void publishToKafka(List<Wind> data) throws JsonProcessingException {
        logger.info("WindCollector: start adding data to kafka message queue ...");

        ObjectMapper objectMapper = new ObjectMapper();

        for (Wind wind: data) {
            String message = objectMapper.writeValueAsString(wind);
            kafkaProducerService.addToMessageQueue(new Message(windKafkaTopic, message));
        }

        logger.info("WindCollector: completed adding data to kafka message queue ...");
    }

    public boolean isEmpty(List<Wind> data) {
        return data.size() == 0;
    }
}
