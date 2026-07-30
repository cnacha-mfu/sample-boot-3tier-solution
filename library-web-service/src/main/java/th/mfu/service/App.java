package th.mfu.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * The REST service. Listens on port 8080.
 *
 * @EntityScan is the line students forget, and it is the first row of the lab's
 * troubleshooting table.
 *
 * @SpringBootApplication only looks for classes underneath ITS OWN package,
 * th.mfu.service. The entities are in th.mfu.domain, in a different module -
 * outside that tree. So Hibernate never sees them, and the app dies with:
 *
 *     Not a managed type: class th.mfu.domain.Book
 *
 * @EntityScan names the package to look in and fixes exactly that.
 *
 * The repositories do NOT need the same treatment: they sit in
 * th.mfu.service.repository, which IS underneath th.mfu.service.
 */
@SpringBootApplication
@EntityScan(basePackages = { "th.mfu.domain" })
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
