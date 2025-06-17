package event;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.*;


/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
class SimpleEventPublisherTest {

    @Test
    void 이벤트_발행_정상작동() {
        // given
        ApplicationEventPublisher springPublisher = mock(ApplicationEventPublisher.class);
        SimpleEventPublisher eventPublisher = new SimpleEventPublisher(springPublisher);

        CustomEvent event = () -> "sample.event";

        // when
        eventPublisher.publish(event);

        // then
        verify(springPublisher, times(1)).publishEvent(event);
    }
}
