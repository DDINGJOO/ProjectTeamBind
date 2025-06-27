package userProfile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import userProfile.entity.UserGerne;
import userProfile.entity.UserProfile;


@Repository
public interface UserGenreRepository extends JpaRepository<UserGerne,Long> {


    void deleteByUserProfile(UserProfile userProfile);

}
