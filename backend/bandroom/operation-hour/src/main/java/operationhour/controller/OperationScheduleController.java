package operationhour.controller;

import dto.operationhours.request.OperationScheduleRequest;
import dto.operationhours.request.TemporaryHolidayRequest;
import dto.operationhours.response.OpenStatusResponse;
import dto.operationhours.response.OperationScheduleResponse;
import dto.operationhours.response.TemporaryHolidayResponse;
import lombok.RequiredArgsConstructor;
import operationhour.service.OperationScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/operation-schedules")
@RequiredArgsConstructor
public class OperationScheduleController {

    private final OperationScheduleService operationScheduleService;

    // 여러 영업시간 등록/수정 요청
    @PostMapping
    public ResponseEntity<List<OperationScheduleResponse>> saveAll(
            @RequestBody List<OperationScheduleRequest> requests) {
        List<OperationScheduleResponse> responses = operationScheduleService.saveAll(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    // 특정 영업시간 조회
    @GetMapping("/{id}")
    public ResponseEntity<OperationScheduleResponse> findById(@PathVariable Long id) {
        Optional<OperationScheduleResponse> response = operationScheduleService.findById(id);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 전체 영업시간 조회
    @GetMapping
    public ResponseEntity<List<OperationScheduleResponse>> findAll() {
        List<OperationScheduleResponse> responses = operationScheduleService.findAll();
        return ResponseEntity.ok(responses);
    }

    // 특정 영업시간 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        operationScheduleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 임시 휴무 등록
    @PostMapping("/temporary-holidays")
    public ResponseEntity<TemporaryHolidayResponse> addTemporaryHoliday(
            @RequestParam Long bandRoomId,
            @RequestParam Long studioId,
            @RequestBody TemporaryHolidayRequest request) {
        TemporaryHolidayResponse response = operationScheduleService.addTemporaryHoliday(bandRoomId, studioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 임시 휴무 삭제
    @DeleteMapping("/temporary-holidays/{holidayId}")
    public ResponseEntity<Void> deleteTemporaryHoliday(@PathVariable Long holidayId) {
        operationScheduleService.deleteTemporaryHoliday(holidayId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/open-check")
    public ResponseEntity<OpenStatusResponse> isOpenAt(
            @RequestParam Long roomId,
            @RequestParam Long studioId,
            @RequestParam String date,
            @RequestParam String time) {
        LocalDate localDate;
        LocalTime localTime;
        try {
            localDate = LocalDate.parse(date);
            localTime = LocalTime.parse(time);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        OpenStatusResponse response = operationScheduleService.isOpenAt(roomId, studioId, localDate, localTime);
        return ResponseEntity.ok(response);
    }

}
