package image;


import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.imageio.ImageIO;
import java.util.Arrays;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableScheduling
@ComponentScan(basePackages = {
        "image",
        "outbox",                // Outbox 모듈
        "primaryIdProvider",
        "inframessaging"
})

@EntityScan(basePackages = {
        "image.entity",
        "outbox"
})
@EnableJpaRepositories(basePackages = {
        "image.repository",
        "outbox",                  // 반드시 outbox 패키지 JPA 리포지토리!
        // 필요하다면 다른 repository 패키지도 추가
})
public class ImageApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 환경 변수 등록 (System 환경에 주입)
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(ImageApplication.class, args);
    }

    @PostConstruct
    public void checkImageIOWriters() {
        System.out.println("==================================================");
        System.out.println("사용 가능한 ImageIO Writer MIME 타입:");
        String[] writerMimeTypes = ImageIO.getWriterMIMETypes();
        System.out.println(Arrays.toString(writerMimeTypes));
        System.out.println("==================================================");
    }

}
