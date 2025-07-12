CREATE TABLE event_outbox (
                              id BIGINT PRIMARY KEY,
                              type VARCHAR(255) NOT NULL,
                              payload TEXT NOT NULL,
                              created_at DATETIME NOT NULL,
                              sent BOOLEAN DEFAULT FALSE,
                              retry_count INT DEFAULT 0,
                              last_error_message TEXT,
                              last_attempt_at DATETIME,
                              dead BOOLEAN DEFAULT FALSE
);