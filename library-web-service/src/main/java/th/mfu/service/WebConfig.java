package th.mfu.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Lets the web page call this service.
 *
 * The page is served from http://localhost:8081 and the service runs on
 * http://localhost:8080. Different port means different ORIGIN, and a browser
 * blocks a page from reading a response from another origin unless the server
 * says it is allowed. That rule is CORS.
 *
 * Without this class the browser console shows:
 *
 *     Access to XMLHttpRequest at 'http://localhost:8080/api/books' from origin
 *     'http://localhost:8081' has been blocked by CORS policy
 *
 * Note that curl and Postman never see this error - only browsers enforce it.
 * Note too that only the named origin is allowed, not "*": this is the
 * three-tier split showing up as a real, visible constraint.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8081")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }
}
