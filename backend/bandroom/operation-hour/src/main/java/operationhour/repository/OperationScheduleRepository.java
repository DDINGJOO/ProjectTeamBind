package operationhour.repository;

import operationhour.entity.OperationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperationScheduleRepository extends JpaRepository<OperationSchedule, Long> {
    // 필요에 따라 쿼리 메서드 추가 가능
    Optional<OperationSchedule> findByRoomIdAndStudioIdAndDayOfWeek(Long roomId, Long studioId, DayOfWeek dayOfWeek);

    List<OperationSchedule> findAllByRoomIdAndStudioId(Long roomId, Long studioId);

}
