package config.oril.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
@EnableHystrix
public class GatewayConfig {
    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .route("todo", r -> r.path("/folders/{folderId}/todo**", "/folders/{folderId}/todo/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://todo"))
                .route("folder", r -> r.path("/folders/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://folder"))

                // .route("resources", r -> r.path("/**")
                //         .filters(f -> f.filter(filter))
                //         .uri("lb://resource"))
                .build();
    }
}
