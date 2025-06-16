package primaryIdProvider;

import java.util.random.RandomGenerator;


/***
 * writer : MyungJoo
 * Date : 2025/06/16
 *
 *
 *
 *
 * 스노우 플레이크를 구현하여 프라이머리 키 생성 전략
 *  ulid 처럼 순서를 갖고있음 + 여러 동시요청 처리 가능
 */
public class Snowflake {

    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

    private final long nodeId = RandomGenerator.getDefault().nextLong(maxNodeId + 1);
    // UTC = 2024-01-01T00:00:00Z
    private final long startTimeMillis = 1704067200000L;

    private long lastTimeMillis = startTimeMillis;
    private long sequence = 0L;





    public synchronized long getNextId() {
        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis < lastTimeMillis) {
            throw new IllegalStateException("Invalid Time");
        }

        if (currentTimeMillis == lastTimeMillis) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                currentTimeMillis = waitNextMillis(currentTimeMillis);
            }
        } else {
            sequence = 0;
        }
        lastTimeMillis = currentTimeMillis;

        return ((currentTimeMillis - startTimeMillis) << (NODE_ID_BITS + SEQUENCE_BITS))
                | (nodeId << SEQUENCE_BITS)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimeMillis) {
            currentTimestamp = System.currentTimeMillis();
        }
        return currentTimestamp;
    }
}