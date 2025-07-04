package operationhour.service;


import dto.operationhours.request.OperationScheduleRequest;
import dto.operationhours.request.TemporaryHolidayRequest;
import dto.operationhours.response.OpenStatusResponse;
import dto.operationhours.response.OperationScheduleResponse;
import dto.operationhours.response.TemporaryHolidayResponse;
import eurm.MonthlyWeekPattern;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import operationhour.entity.OperationSchedule;
import operationhour.entity.TemporaryHoliday;
import operationhour.repository.OperationScheduleRepository;
import operationhour.repository.TemporaryHolidayRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing operation schedules and handling related business logic.
 *
 * This service provides methods for creating, updating, deleting, and querying operation schedules,
 * as well as managing temporary holidays and checking open status based on specified criteria.
 *
 * The key functionalities include:
 * - Managing operation schedules, including saving multiple schedules, retrieving a single schedule
 *   by ID, or retrieving all schedules.
 * - Adding or deleting temporary holidays associated with specific operation schedules.
 * - Determining the open status of a studio or room at a particular date and time, taking into account
 *   operation schedules, temporary holidays, and monthly week patterns.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OperationScheduleService {

    private final OperationScheduleRepository operationScheduleRepository;
    private final TemporaryHolidayRepository temporaryHolidayRepository;

    private OperationScheduleResponse toResponse(OperationSchedule schedule) {
        return OperationScheduleResponse.builder()
                .id(schedule.getId())
                .roomId(schedule.getRoomId())
                .studioId(schedule.getStudioId())
                .price(schedule.getPrice())
                .dayOfWeek(schedule.getDayOfWeek())
                .openTime(schedule.getOpenTime())
                .closeTime(schedule.getCloseTime())
                .monthlyWeekPattern(schedule.getMonthlyWeekPattern())
                .active(schedule.isActive())
                .temporaryHolidays(schedule.getTemporaryHolidays().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private TemporaryHolidayResponse toResponse(TemporaryHoliday holiday) {
        return TemporaryHolidayResponse.builder()
                .id(holiday.getId())
                .holidayDate(holiday.getHolidayDate())
                .reason(holiday.getReason())
                .build();
    }

    private void updateEntityFromRequest(OperationSchedule schedule, OperationScheduleRequest request) {
        schedule.setRoomId(request.getRoomId());
        schedule.setStudioId(request.getStudioId());
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setOpenTime(request.getOpenTime());
        schedule.setCloseTime(request.getCloseTime());
        schedule.setMonthlyWeekPattern(request.getMonthlyWeekPattern());
        schedule.setActive(request.getActive());
    }

    // 여러 OperationScheduleRequest 저장 및 수정
    public List<OperationScheduleResponse> saveAll(List<OperationScheduleRequest> requests) {
        List<OperationSchedule> schedules = requests.stream()
                .map(req -> {
                    OperationSchedule schedule = new OperationSchedule();
                    updateEntityFromRequest(schedule, req);
                    return schedule;
                })
                .collect(Collectors.toList());

        List<OperationSchedule> saved = operationScheduleRepository.saveAll(schedules);
        return saved.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ID로 단건 조회는 그대로 둠
    public Optional<OperationScheduleResponse> findById(Long id) {
        return operationScheduleRepository.findById(id)
                .map(this::toResponse);
    }

    // 전체 조회도 그대로 둠
    public List<OperationScheduleResponse> findAll() {
        return operationScheduleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        operationScheduleRepository.deleteById(id);
    }
    public TemporaryHolidayResponse addTemporaryHoliday(Long bandRoomId, Long studioId, TemporaryHolidayRequest request) {
        // 요청 날짜의 요일을 활용해서 영업 스케줄 찾기 (예: 임시 휴무 등록 시점의 날짜 기반)
        // 만약 요청에서 특정 요일을 받는다면 그걸 써야 합니다.
        // 여기선 임시 휴무 날짜의 요일로 조회합니다.
        DayOfWeek dayOfWeek = request.getHolidayDate().getDayOfWeek();

        OperationSchedule schedule = operationScheduleRepository
                .findByRoomIdAndStudioIdAndDayOfWeek(bandRoomId, studioId, dayOfWeek)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RoomId/StudioId/DayOfWeek combination"));

        TemporaryHoliday holiday = TemporaryHoliday.builder()
                .operationSchedule(schedule)
                .holidayDate(request.getHolidayDate())
                .reason(request.getReason())
                .build();

        TemporaryHoliday saved = temporaryHolidayRepository.save(holiday);
        return toResponse(saved);
    }


    public void deleteTemporaryHoliday(Long holidayId) {
        temporaryHolidayRepository.deleteById(holidayId);
    }

    public OpenStatusResponse isOpenAt(Long roomId, Long studioId, LocalDate date, LocalTime time) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        Optional<OperationSchedule> optionalSchedule = operationScheduleRepository
                .findByRoomIdAndStudioIdAndDayOfWeek(roomId, studioId, dayOfWeek);

        if (optionalSchedule.isEmpty())
            return new OpenStatusResponse(false, 0L);

        OperationSchedule schedule = optionalSchedule.get();

        if (!schedule.isActive())
            return new OpenStatusResponse(false, 0L);

        // 임시휴무 체크
        boolean isTemporaryHoliday = schedule.getTemporaryHolidays().stream()
                .anyMatch(h -> h.getHolidayDate().equals(date));
        if (isTemporaryHoliday)
            return new OpenStatusResponse(false, 0L);

        // 매달 주차별 휴무 패턴 체크
        if (isClosedDueToMonthlyWeek(date, schedule))
            return new OpenStatusResponse(false, 0L);

        // 영업 시간 체크
        LocalTime open = schedule.getOpenTime();
        LocalTime close = schedule.getCloseTime();

        boolean isOpen = !time.isBefore(open) && !time.isAfter(close);

        return new OpenStatusResponse(isOpen, isOpen ? schedule.getPrice() : 0L);
    }


    private boolean isClosedDueToMonthlyWeek(LocalDate date, OperationSchedule schedule) {
        MonthlyWeekPattern pattern = schedule.getMonthlyWeekPattern();

        if (pattern == MonthlyWeekPattern.NONE) {
            return false;
        }

        int weekOfMonth = (date.getDayOfMonth() - 1) / 7 + 1;

        return switch (pattern) {
            case WEEK_1_3_5 -> (weekOfMonth == 1 || weekOfMonth == 3 || weekOfMonth == 5);
            case WEEK_2_4 -> (weekOfMonth == 2 || weekOfMonth == 4);
            default -> false;
        };
    }
}
