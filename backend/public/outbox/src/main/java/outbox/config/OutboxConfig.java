package outbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import outbox.publisher.OutboxEventPublisher;
import outbox.repository.OutboxEventRepository;
import primaryIdProvider.Snowflake;

@Configuration
public class OutboxConfig {

    @Bean
    public OutboxEventPublisher outboxEventPublisher(
            OutboxEventRepository repository,
            Snowflake snowflake
    ) {
        return new OutboxEventPublisher(repository, snowflake);
    }
}