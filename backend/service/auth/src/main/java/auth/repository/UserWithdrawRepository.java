package auth.repository;


import auth.entity.UserWithdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserWithdrawRepository  extends JpaRepository<UserWithdraw,Long> {


    List<UserWithdraw> findByWithdrawDateBefore(LocalDateTime cutoff);


}
