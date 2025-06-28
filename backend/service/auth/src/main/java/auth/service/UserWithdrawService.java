package auth.service;


import auth.entity.UserWithdraw;
import auth.repository.UserRepository;
import auth.repository.UserWithdrawRepository;
import exception.error_code.auth.AuthErrorCode;
import exception.excrptions.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserWithdrawService {

    private final UserRepository userRepository;
    private final UserWithdrawRepository userWithdrawRepository;


    @Transactional
    public void withdraw(Long userId, String reason) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if(user.getIsDeleted())
        {
            throw new AuthException(AuthErrorCode.DELETED_USER);
        }

        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);

        var userWithdraw = UserWithdraw.builder()
                .userId(userId)
                .reason(reason)
                .withdrawDate(LocalDateTime.now())
                .build();

        userWithdrawRepository.save(userWithdraw);
    }




    @Scheduled(cron = "0 0 5 * * *") // 매일 오전 5시 실행
    @Transactional
    public void deleteWithdrawnUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        List<UserWithdraw> withdraws = userWithdrawRepository.findByWithdrawDateBefore(cutoff);
        for (UserWithdraw withdraw : withdraws) {
            userRepository.deleteById(withdraw.getUserId());
        }
    }
}




