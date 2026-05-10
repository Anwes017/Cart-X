package com.ecommerce.notification.config;

import com.ecommerce.payment.event.OrderPaidEvent;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;


@Configuration
public class KafkaConsumerConfig {
    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition(
                                "notification.dlq",
                                record.partition()
                        )
                );

        FixedBackOff backOff = new FixedBackOff(
                2000L, // 2 seconds
                3L     // 3 retries (total 4 attempts)
        );

        return new DefaultErrorHandler(recoverer, backOff);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPaidEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, OrderPaidEvent> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, OrderPaidEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}