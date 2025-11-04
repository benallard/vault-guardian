package de.benallard.occurrent.eventstore.spring.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.benallard.occurent.eventstore.spring.data.SpringDataJpaEventStoreConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@Configuration
@EnableAutoConfiguration
@Import(SpringDataJpaEventStoreConfiguration.class)
public class SpringBootConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
