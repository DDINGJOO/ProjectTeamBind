package userProfile.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import userProfile.entity.UserInterest;
import userProfile.entity.UserProfile;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterest,Long> {
    void deleteByUserProfile(UserProfile userProfile);




}
