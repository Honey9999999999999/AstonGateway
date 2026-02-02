package org.example.filter;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.core.io.buffer.DataBufferUtils;
import java.nio.charset.StandardCharsets;

@Component
public class KafkaPublishFilter extends AbstractGatewayFilterFactory<KafkaPublishFilter.Config> {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPublishFilter(KafkaTemplate<String, String> kafkaTemplate) {
        super(Config.class);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Читаем тело POST-запроса
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String body = new String(bytes, StandardCharsets.UTF_8);

                        // Отправляем в Kafka
                        kafkaTemplate.send(config.getTopic(), body);

                        // Освобождаем память
                        DataBufferUtils.release(dataBuffer);

                        // Отвечаем 202 Accepted
                        exchange.getResponse().setStatusCode(HttpStatus.ACCEPTED);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    @Data
    public static class Config {
        private String topic;
    }
}
