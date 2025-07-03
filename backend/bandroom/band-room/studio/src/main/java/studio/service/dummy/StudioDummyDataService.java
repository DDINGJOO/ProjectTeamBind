package studio.service.dummy;


import eurm.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import primaryIdProvider.Snowflake;
import studio.entity.Image;
import studio.entity.Studio;
import studio.repository.StudioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StudioDummyDataService {

    private final StudioRepository studioRepository;
    private final Snowflake snowflake;
    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
    );

    /**
     * bandRoomId 목록을 받아 각 아이디당 createStudiosForBandRoom 을 병렬 실행
     */
    @Transactional
    public void generateDummyStudios(List<Long> bandRoomIds) throws InterruptedException {
        List<Callable<Void>> tasks = bandRoomIds.stream()
                .map(id -> (Callable<Void>) () -> {
                    createStudiosForBandRoom(id);
                    return null;
                }).toList();

        // invokeAll: 모든 작업 제출 후 완료 대기
        List<Future<Void>> results = executor.invokeAll(tasks);

        // (선택) 개별 작업 예외 체크
        for (Future<Void> f : results) {
            try { f.get(); }
            catch (ExecutionException e) {
                // 로깅하거나 재시도 로직 등 처리
                e.printStackTrace();
            }
        }
    }

    /**
     * 단일 bandRoomId 당 100개의 Studio 생성 & 저장,
     * 각 Studio마다 3개의 Image 생성
     */
    @Transactional
    public void createStudiosForBandRoom(long bandRoomId) {
        List<Studio> studios = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            long studioId = snowflake.nextId();
            Studio studio = Studio.builder()
                    .id(studioId)
                    .bandRoomId(bandRoomId)
                    .name("Studio " + bandRoomId + "-" + i)
                    .description("더미 스튜디오 " + bandRoomId + "-" + i)
                    .status(Status.open)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // **이미지 3개** 생성
            int finalI = i;
            List<Image> images = IntStream.rangeClosed(1, 3)
                    .mapToObj(j -> {
                        long imageId = snowflake.nextId();
                        return Image.builder()
                                .id(imageId)
                                .url("https://dummyimage.com/600x400/000/fff&text=SR"
                                        + bandRoomId + "-" + finalI + "-" + j)
                                .studio(studio)
                                .build();
                    })
                    .toList();

            studio.setImages(images);
            studios.add(studio);
        }

        studioRepository.saveAll(studios);
    }
}
