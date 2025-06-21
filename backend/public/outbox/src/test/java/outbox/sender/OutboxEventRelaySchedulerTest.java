package outbox.sender;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import outbox.config.OutboxStatus;
import outbox.entity.OutboxEventEntity;
import outbox.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("OutboxEventRelayScheduler 테스트")
class OutboxEventRelaySchedulerTest {

    @Mock
    private OutboxEventRepository repository;

    @Mock
    private MessagingSender sender;

    @InjectMocks
    private OutboxEventRelayScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("PENDING 상태의 이벤트를 성공적으로 전송하고 SENT로 변경한다")
    void relayEvents_successful() {
        OutboxEventEntity pendingEvent = mock(OutboxEventEntity.class);
        when(pendingEvent.getTopic()).thenReturn("test-topic");
        when(pendingEvent.getPayload()).thenReturn("{...}");

        when(repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(pendingEvent));

        scheduler.relayEvents();

        verify(sender).send(eq("test-topic"), eq("{...}"), isNull());
        verify(pendingEvent).markSuccess();
        verify(repository).saveAll(anyList());
    }

    @Test
    @DisplayName("이벤트 전송에 실패하면 상태를 FAILED로 변경한다")
    void relayEvents_failure() {
        OutboxEventEntity pendingEvent = mock(OutboxEventEntity.class);
        when(pendingEvent.getTopic()).thenReturn("test-topic");
        when(pendingEvent.getPayload()).thenReturn("{...}");

        when(repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(pendingEvent));

        doThrow(new RuntimeException("Kafka 실패")).when(sender).send(any(), any(), any());

        scheduler.relayEvents();

        verify(pendingEvent).markFailure(contains("Kafka 실패"));
        verify(repository).saveAll(anyList());
    }

    @Test
    @DisplayName("1시간 이상 지난 SENT 상태의 이벤트는 삭제된다")
    void deleteOldSentEvents_shouldDeleteOldSentEvents() {
        // given
        LocalDateTime fixedNow = LocalDateTime.of(2025, 6, 21, 13, 0); // 고정 시간
        LocalDateTime expectedCutoff = fixedNow.minusHours(1);

        try (MockedStatic<LocalDateTime> mockedNow = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            mockedNow.when(LocalDateTime::now).thenReturn(fixedNow);

            // when
            scheduler.deleteOldSentEvents();

            // then
            ArgumentCaptor<OutboxStatus> statusCaptor = ArgumentCaptor.forClass(OutboxStatus.class);
            ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(repository).deleteByStatusAndUpdatedAtBefore(statusCaptor.capture(), timeCaptor.capture());

            assertEquals(OutboxStatus.SENT, statusCaptor.getValue());
            assertEquals(expectedCutoff, timeCaptor.getValue());
        }
    }


}
