package userProfile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import userProfile.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile,Long> ,UserProfileRepositoryCustom {
}
