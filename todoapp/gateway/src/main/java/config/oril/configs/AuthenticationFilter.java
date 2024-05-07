package config.oril.configs;


import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import com.example.aspect.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouterValidator validator;

    private JwtUtil jwtUtils = new JwtUtil();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (!validator.isNotSecured.test(request)) {
            
            if (authMissing(request)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            final String token = request.getHeaders().getOrEmpty("Authorization").get(0);

            if (this.jwtUtils.isExpired(token)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            
        }

        return chain.filter(exchange);
    }

    public Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {

        
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(httpStatus);
        // Set the content type to HTML
        response.getHeaders().setContentType(MediaType.TEXT_HTML);

        // Set the uri to login page
        URI loginUri = URI.create("/login");
        response.getHeaders().setLocation(loginUri);
        

        // Load the HTML file from the resources/static folder
        ClassPathResource resource = new ClassPathResource("static/login.html");

        try {
            // Read the HTML file content
            byte[] bytes = resource.getInputStream().readAllBytes();
            String htmlContent = new String(bytes, StandardCharsets.UTF_8);

            // Write the HTML content to the response
            return response.writeWith(Mono.just(response.bufferFactory().wrap(htmlContent.getBytes())));
        } catch (IOException e) {
            // Handle error reading HTML file
            return response.setComplete();
        }
    }

    private boolean authMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
}
