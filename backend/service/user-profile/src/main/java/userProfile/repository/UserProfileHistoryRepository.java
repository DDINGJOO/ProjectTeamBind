package userProfile.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import userProfile.entity.UserProfileHistory;

import java.util.List;

@Repository
public interface UserProfileHistoryRepository extends JpaRepository<UserProfileHistory,Long> {

    List<UserProfileHistory> findAllByUserIdOrderByChangedAtDesc(Long userId);

}
