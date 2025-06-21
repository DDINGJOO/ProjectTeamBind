package primaryIdProvider;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Author: MyungJoo
 * Date: 2025-06-17
 */
public class SnowflakeConcurrentTest {

    private final Snowflake snowflake = new Snowflake();

    @Test
    void generateIdsConcurrently_withoutDuplicates() throws InterruptedException {
        int threadCount = 100;
        int idsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        Set<Long> allIds = Collections.synchronizedSet(new HashSet<>());
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    long id = snowflake.nextId();
                    allIds.add(id);
                }
                latch.countDown();
            });
        }

        latch.await(); // 모든 스레드 종료까지 대기
        executor.shutdown();

        int expected = threadCount * idsPerThread;
        assertEquals(expected, allIds.size(), "ID 중복 발생 가능성 있음");

        System.out.println("Generated " + allIds.size() + " unique IDs successfully.");
    }
}