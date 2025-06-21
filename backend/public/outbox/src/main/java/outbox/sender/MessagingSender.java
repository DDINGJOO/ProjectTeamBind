package outbox.sender;


public interface MessagingSender {
    void send(String topic, String payload, String key);


}