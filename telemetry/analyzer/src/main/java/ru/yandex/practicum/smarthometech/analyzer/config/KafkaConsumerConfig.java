package ru.yandex.practicum.smarthometech.analyzer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import ru.yandex.practicum.kafka.telemetry.deserializer.HubEventDeserializer;
import ru.yandex.practicum.kafka.telemetry.deserializer.SensorSnapshotDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // --- FACTORY FOR HUB EVENTS ---
    @Bean
    public KafkaListenerContainerFactory<?> hubEventsContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(hubEventsConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> hubEventsConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }


    // --- FACTORY FOR SNAPSHOT EVENTS ---
    @Bean
    public KafkaListenerContainerFactory<?> snapshotsContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snapshotsConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> snapshotsConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorSnapshotDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }
}