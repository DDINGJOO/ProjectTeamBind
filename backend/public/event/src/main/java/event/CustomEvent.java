package event;


/**
 * Author : MyungJoo
 * Date : 2026/06/117
 */
public interface CustomEvent {
    String name();        // 이벤트명
    String getTopic();    // Kafka topic
}