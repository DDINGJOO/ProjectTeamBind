package outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import outbox.entity.OutboxEventEntity;

import java.util.List;


@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {
    List<OutboxEventEntity> findTop100BySentFalseOrderByCreatedAtAsc();
}