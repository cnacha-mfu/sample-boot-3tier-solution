package th.mfu.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Serves the web page on port 8081. That is all it does.
 *
 * There is no controller here, and no database. Spring Boot publishes anything
 * under src/main/resources/static/ by itself, so library.html is reachable at
 * http://localhost:8081/library.html
 */
@SpringBootApplication
public class FrontApp {
    public static void main(String[] args) {
        SpringApplication.run(FrontApp.class, args);
    }
}
