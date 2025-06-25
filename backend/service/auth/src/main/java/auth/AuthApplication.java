package auth;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = {
        "auth",
        "outbox",                // Outbox 모듈
        "primaryIdProvider",
        "token",
        "inframessaging"
})

@EntityScan(basePackages = {
        "auth.entity",
        "outbox"
})
@EnableJpaRepositories(basePackages = {
        "auth.repository",   // 이미지 도메인 JPA 레포지토리
        "outbox",                  // 반드시 outbox 패키지 JPA 리포지토리!
        // 필요하다면 다른 repository 패키지도 추가
})
public class AuthApplication {
    public static void  main(String[] args)
    {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 환경 변수 등록 (System 환경에 주입)
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );


        SpringApplication.run(AuthApplication.class, args);
    }
}