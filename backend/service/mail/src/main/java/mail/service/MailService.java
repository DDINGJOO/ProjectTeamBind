package mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void confirmedEmail(String email, Long userId) {
        System.out.println("This is MailService.sendVerificationEmail() \n email : " + email + "\n token :" + userId);
        try {
            //TODO : 인증 링크를 그냥 만들어서 줄까?
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[BANDER] 회원가입 이메일 인증");
            helper.setText("<h1>이메일 인증</h1>" +
                            "<p>아래 링크를 클릭하여 이메일을 인증해주세요:</p>" +
                            "<a href='http://localhost:9001/api/confirm/v1/email?" +userId + "'>이메일 인증하기</a>",
                    true);

            mailSender.send(message);
            log.info("이메일 전송 성공: {}", email);

        } catch (MessagingException e) {
            log.error("이메일 전송 실패", e);
        }
    }


}