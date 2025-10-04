package de.benallard.vaultguardian;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.benallard.vaultguardian.events.CashBoxEvent;
import org.occurrent.application.converter.CloudEventConverter;
import org.occurrent.application.converter.jackson.JacksonCloudEventConverter;
import org.occurrent.application.service.blocking.ApplicationService;
import org.occurrent.application.service.blocking.generic.GenericApplicationService;
import org.occurrent.eventstore.api.blocking.EventStore;
import org.occurrent.eventstore.inmemory.InMemoryEventStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URI;

@SpringBootApplication
public class VaultGuardianApplication {
    public static void main(String[] args) {
        SpringApplication.run(VaultGuardianApplication.class, args);
    }

    @Bean
    public EventStore myEventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    CloudEventConverter<CashBoxEvent> converter(ObjectMapper objectMapper){
        URI cloudEventSource = URI.create("urn:company:domain");
        return new JacksonCloudEventConverter<>(objectMapper, cloudEventSource);
    }

    @Bean
    public ApplicationService<CashBoxEvent> applicationService(EventStore eventStore, CloudEventConverter<CashBoxEvent> eventConverter) {
        return new GenericApplicationService<CashBoxEvent>(eventStore, eventConverter);
    }
}