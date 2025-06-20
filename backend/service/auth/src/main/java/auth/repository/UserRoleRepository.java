package auth.repository;


import auth.entity.User;
import auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, User>{
    Optional<UserRole> findByUser(User user);

}
