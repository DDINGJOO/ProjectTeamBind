package operationhour.repository;

import operationhour.entity.TemporaryHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TemporaryHolidayRepository extends JpaRepository<TemporaryHoliday, Long> {

    // 특정 운영시간(OperationSchedule)과 날짜로 임시휴일 검색
    List<TemporaryHoliday> findByOperationScheduleIdAndHolidayDate(Long operationScheduleId, LocalDate holidayDate);

    // 특정 운영시간에 속한 모든 임시휴일 조회
    List<TemporaryHoliday> findByOperationScheduleId(Long operationScheduleId);
}
