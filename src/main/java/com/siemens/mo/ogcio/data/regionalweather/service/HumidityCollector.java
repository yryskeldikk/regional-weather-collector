package com.siemens.mo.ogcio.data.regionalweather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.mo.ogcio.data.regionalweather.bean.Humidity;
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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "app", name = "collector.active", havingValue = "humidity")
public class HumidityCollector {

    private final Logger logger = LoggerFactory.getLogger(AirTemperatureCollector.class);

    @Value("${app.api.regionalweather.humidity.url}")
    private String humidityUrl;

    @Value("${spring.cloud.stream.kafka.binder.topic.humidity}")
    private String humidityKafkaTopic;

    @Value("${app.api.regionalweather.retry.count}")
    private int retryCount;

    @Value("${app.api.regionalweather.retry.delay}")
    private int retryDelay;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Resource
    RestTemplate restTemplate;

    @Scheduled(cron = "${app.api.regionalweather.humidity.cron}")
    public void scheduledCollect() throws Exception {
        collect();
    }

    public void collect() {
        int successCount = 0;

        Map.Entry<Void,Throwable> stopExecution = retryAble( () -> {
            List<Humidity> data = collectHumidityData();
            if( isEmpty(data) ){
                throw new Exception("HumidityCollector: data is still empty after retrieve process" );
            }
            return null;
        }, retryCount, retryDelay );
        if( stopExecution.getValue() != null ){
            logger.error(
                    "HumidityCollector: Encountered error while collecting data after retrying " + retryCount + " times with delay " + retryDelay + "ms",
                    stopExecution.getValue()
            );
        } else {
            successCount++;
        }

        logger.info("HumidityCollector: completed collecting data successfully ...");
    }

    public static <T> Map.Entry<T,Throwable> retryAble(WindCollector.ThrowableSupplier<T> supplier, int retryCount, long delay_ms ){
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

    public List<Humidity> collectHumidityData() throws JsonProcessingException, ApiRetrievalException {

        logger.info("HumidityCollector: start retrieving data from API ...");

        ResponseEntity<String> response = restTemplate.getForEntity(humidityUrl, String.class);
        if (response.getStatusCode() != HttpStatus.OK)
            throw new ApiRetrievalException(response.getStatusCode());

        String[] data = response.getBody().split("\\r?\\n|\\r");

        List<Humidity> result = new ArrayList<>();

        // start from 1 skipping the header
        for (int i = 1; i < data.length; i++) {
            String[] line = data[i].split(",");
            Humidity humidity = new Humidity();
            humidity.setTimestamp(DateUtil.toUTC(line[0]));
            humidity.setAutomaticWeatherStation(line[1]);
            humidity.setRelativeHumidity(line[2].equals("N/A") ? null : Integer.parseInt(line[2]));
            result.add(humidity);
        }

        logger.info("HumidityCollector: successfully retrieved data of size {}", result.size());

        publishToKafka(result);

        return result;
    }

    public void publishToKafka(List<Humidity> data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        logger.info("HumidityCollector: start adding data to kafka message queue");

        for (Humidity humidity: data) {
            String message = objectMapper.writeValueAsString(humidity);
            kafkaProducerService.addToMessageQueue(new Message(humidityKafkaTopic, message));
        }

        logger.info("HumidityCollector: completed adding data to kafka message queue");
    }

    public boolean isEmpty(List<Humidity> data) { return data.size() == 0; }


}
