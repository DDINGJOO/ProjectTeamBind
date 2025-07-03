package studio.controller.dummy;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import studio.service.dummy.StudioDummyDataService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@RestController
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class StudioDummyDataController {

    private final StudioDummyDataService dummyDataService;

    /**
     * POST /api/studios/dummy
     * 호출 시 100개의 Studio, 각 Studio에 100개의 Image 더미 생성
     */
    @GetMapping()
    public ResponseEntity<String> createDummyMultiThread() throws InterruptedException {
        // 1 ~ 100 까지 밴드룸 아이디 리스트
        List<Long> bandRoomIds = IntStream.rangeClosed(1, 100)
                .mapToObj(Long::valueOf)
                .collect(Collectors.toList());

        dummyDataService.generateDummyStudios(bandRoomIds);
        return ResponseEntity.ok("멀티스레드 더미 스튜디오 생성 완료 (100개의 bandRoomId × 100개)");
    }
}