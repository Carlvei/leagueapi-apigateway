package at.adesso.leagueapi.apigateway.gateway;

import at.adesso.leagueapi.commons.errorhandling.exceptions.UnauthorizedAccessException;
import at.adesso.leagueapi.commons.util.jwt.JwtTokenValidator;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthorizationFilter implements WebFilter {
    private final List<String> AUTHENTICATIONLESS_PATHS = List.of("/login");
    private final JwtTokenValidator jwtTokenValidator;

    public AuthorizationFilter(final JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }


    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final String path = exchange.getRequest().getPath().value();
        if (AUTHENTICATIONLESS_PATHS.contains(path) &&
                (authorizationHeader == null || authorizationHeader.isEmpty())) {
            return chain.filter(exchange);
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedAccessException("No Bearertoken was found in the headers.");
        }

        final String token = authorizationHeader.split(" ")[1].trim();
        if (!jwtTokenValidator.validateToken(token)) {
            throw new UnauthorizedAccessException("Token has expired");
        }
        return chain.filter(exchange);
    }
}
