package userActivityLog.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import userActivityLog.entity.UserActivityLog;


@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
}