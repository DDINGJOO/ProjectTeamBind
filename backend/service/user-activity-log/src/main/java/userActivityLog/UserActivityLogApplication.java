package userActivityLog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = {
        "userActivityLog",
        "primaryIdProvider",
})

@EntityScan(basePackages = {
        "userActivityLog.entity",
        "outbox"
})
@EnableJpaRepositories(basePackages = {
        "userActivityLog.repository",   // 이미지 도메인 JPA 레포지토리
})
public class UserActivityLogApplication {
    public static void  main(String[] args)
    {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 환경 변수 등록 (System 환경에 주입)
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );


        SpringApplication.run(UserActivityLogApplication.class, args);
    }
}