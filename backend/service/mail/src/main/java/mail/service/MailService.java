package mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${bander.app.url}")
    private String banderAppUrl;

    public void confirmedEmail(Long userId,String email, String code) {
        System.out.println("This is MailService.sendVerificationEmail() \n email : " + email + "\n code :" + code + "\n userId: " + userId);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[BANDER] 회원가입 이메일 인증");

            String verificationUrl = banderAppUrl + "/api/auth/v1/confirmEmail";

            helper.setText("<h1>이메일 인증</h1>" +
                            "<p>아래 인증 코드를 확인하고 입력란에 입력한 후, '인증하기' 버튼을 눌러주세요.</p>" +
                            "<p style='font-size: 1.2em; font-weight: bold;'>인증 코드: " + code + "</p>" +
                            "<form action='" + verificationUrl + "' method='post' style='margin-top: 20px;'>" +
                            "    <input type='hidden' name='userId' value='" + userId + "'>" +
                            "    <div>" +
                            "        <label for='code' style='font-weight: bold;'>인증 코드 입력:</label><br>" +
                            "        <input type='text' id='code' name='code' required style='padding: 8px; margin-top: 5px; margin-bottom: 15px; width: 200px;'>" +
                            "    </div>" +
                            "    <input type='submit' value='인증하기' style='padding: 10px 20px; background-color: #007bff; color: white; border: none; border-radius: 5px; cursor: pointer;'>" +
                            "</form>",
                    true);

            mailSender.send(message);
            log.info("이메일 전송 성공: {}", email);

        } catch (MessagingException e) {
            log.error("이메일 전송 실패", e);
        }
    }




}