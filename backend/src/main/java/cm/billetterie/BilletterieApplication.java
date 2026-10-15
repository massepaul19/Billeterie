package cm.billetterie;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BilletterieApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Douala"));
        SpringApplication.run(BilletterieApplication.class, args);
    }
}
