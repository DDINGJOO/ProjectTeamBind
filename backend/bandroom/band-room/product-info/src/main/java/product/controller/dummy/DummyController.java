package product.controller.dummy;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import product.service.dummy.DummyDataService;

@RestController
@RequestMapping("/dummy")
@RequiredArgsConstructor
public class DummyController
{
    private final DummyDataService dummyDataInitializer;

    @GetMapping()
    public ResponseEntity<String> initDummy() {
        dummyDataInitializer.initDummyData();
        return ResponseEntity.ok("더미 데이터 생성 완료");
    }
}
