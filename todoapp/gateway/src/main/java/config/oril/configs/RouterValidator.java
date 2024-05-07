package config.oril.configs;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouterValidator {

    public static final List<String> openEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/register",
            "/login",
            "/"
    );

    public Predicate<ServerHttpRequest> isNotSecured =
            request -> openEndpoints.stream()
                    .anyMatch(uri -> request.getURI().getPath().equals(uri));
}
