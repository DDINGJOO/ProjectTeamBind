package userProfile.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import userProfile.entity.UserProfileHistory;

@Repository
public interface userProfileHistoryRepository extends JpaRepository<UserProfileHistory,Long> {

}
