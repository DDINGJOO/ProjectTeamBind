package outbox.publisher;

import com.fasterxml.jackson.annotation.JsonProperty;
import event.CustomEvent;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import outbox.config.OutboxStatus;
import outbox.entity.OutboxEventEntity;
import outbox.repository.OutboxEventRepository;
import primaryIdProvider.Snowflake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("OutboxEventPublisher 테스트")
class OutboxEventPublisherTest {

    @Mock private OutboxEventRepository repository;
    @Mock private Snowflake snowflake;
    @InjectMocks private OutboxEventPublisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이벤트를 PENDING 상태로 저장한다")
    void publish_shouldStoreEventWithPendingStatus() {
        CustomEvent event = new TestCreatedEvent(1L, "test");
        when(snowflake.nextId()).thenReturn(123L);

        publisher.publish(event);

        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(repository).save(captor.capture());

        OutboxEventEntity saved = captor.getValue();
        assertEquals("TEST_CREATED", saved.getTopic());
        assertEquals(OutboxStatus.PENDING, saved.getStatus());
    }






}


@Getter
class TestCreatedEvent implements CustomEvent {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    public TestCreatedEvent(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TestCreatedEvent() {
        // Jackson 기본 생성자
    }

    @Override
    public String name() {
        return "TEST_CREATED";
    }

    @Override
    public String getTopic() {
        return "log.save"; //
    }
}