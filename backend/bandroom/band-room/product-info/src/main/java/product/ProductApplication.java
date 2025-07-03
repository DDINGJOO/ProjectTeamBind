package product;

import io.github.cdimascio.dotenv.Dotenv;
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
        "product",
        "outbox",                // Outbox 모듈
        "primaryIdProvider",
        "wordFilter",
        "event",
        "inframessaging",
        "dataserializer",
})

@EntityScan(basePackages = {
        "product.entity",
        "outbox"
})
@EnableJpaRepositories(basePackages = {
        "product.repository",
        "outbox",
        // 필요하다면 다른 repository 패키지도 추가
})
public class ProductApplication {
    public static void main(String[] args) {
        {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            // 환경 변수 등록 (System 환경에 주입)
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );

            SpringApplication.run(ProductApplication.class, args);
        }

    }
}
