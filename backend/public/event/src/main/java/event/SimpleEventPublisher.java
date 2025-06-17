package event;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;



/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
@RequiredArgsConstructor
public class SimpleEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher springPublisher;

    @Override
    public void publish(CustomEvent event) {
        springPublisher.publishEvent(event); // Spring 내부 이벤트 발행
    }
}