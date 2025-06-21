package outbox.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import outbox.config.OutboxStatus;
import outbox.entity.OutboxEventEntity;

import java.time.LocalDateTime;
import java.util.List;


public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {

    List<OutboxEventEntity> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    @Transactional
    @Modifying
    @Query("DELETE FROM OutboxEventEntity e WHERE e.status = :status AND e.updatedAt < :cutoff")
    int deleteByStatusAndUpdatedAtBefore(@Param("status") OutboxStatus status, @Param("cutoff") LocalDateTime cutoff);

}