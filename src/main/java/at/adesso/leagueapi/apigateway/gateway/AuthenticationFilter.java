package at.adesso.leagueapi.apigateway.gateway;

import at.adesso.leagueapi.commons.errorhandling.exceptions.UnauthorizedAccessException;
import at.adesso.leagueapi.commons.security.JwtSecurityFilter;
import at.adesso.leagueapi.commons.util.jwt.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter implements WebFilter {
    private final List<String> pathsWithoutAuthentication;
    private final JwtTokenValidator jwtTokenValidator;

    public AuthenticationFilter(@Value("${leagueapi.gateway.paths-without-authentication}") final List<String> pathsWithoutAuthentication,
                                final JwtTokenValidator jwtTokenValidator) {
        this.pathsWithoutAuthentication = pathsWithoutAuthentication;
        this.jwtTokenValidator = jwtTokenValidator;
    }


    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final HttpCookie accessTokenCookie = exchange.getRequest()
                .getCookies()
                .getFirst(JwtSecurityFilter.ACCESS_TOKEN_NAME);

        final String path = exchange.getRequest()
                .getPath()
                .value();
        if (pathsWithoutAuthentication.contains(path)) {
            return chain.filter(exchange);
        }

        if (accessTokenCookie == null) {
            throw new UnauthorizedAccessException("No access token was found in the headers.");
        }

        final String token = accessTokenCookie.getValue();
        if (!jwtTokenValidator.validateToken(token)) {
            throw new UnauthorizedAccessException("Token has expired");
        }
        return chain.filter(exchange);
    }
}
