package mail;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "mail",
        "outbox",                // Outbox 모듈
        "primaryIdProvider",
})

@EntityScan(basePackages = {
        "mail.entity",
        "outbox"
})
@EnableJpaRepositories(basePackages = {
        "outbox",                  // 반드시 outbox 패키지 JPA 리포지토리!
        // 필요하다면 다른 repository 패키지도 추가
})
public class MailApplication {
    public static void  main(String[] args)
    {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 환경 변수 등록 (System 환경에 주입)
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );


        SpringApplication.run(MailApplication.class, args);
    }
}