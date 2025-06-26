package image;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

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
        "outbox",                  // 반드시 outbox 패키지 JPA 리포지토리!
        // 필요하다면 다른 repository 패키지도 추가
})
public class ImageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageApplication.class, args);
    }
}
