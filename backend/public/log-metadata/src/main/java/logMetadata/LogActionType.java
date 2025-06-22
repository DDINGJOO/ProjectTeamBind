package logMetadata;

public enum LogActionType {
    VIEW_BANDROOM(LogTopic.ACTIVITY_LOG_SAVE),
    SEARCH_ADDRESS(LogTopic.ACTIVITY_LOG_SAVE),
    DELETE_POST(LogTopic.ACTIVITY_LOG_SAVE),;

    private final String topic;

    LogActionType(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}