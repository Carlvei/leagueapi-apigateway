package at.adesso.leagueapi.apigateway.config;

import at.adesso.leagueapi.apigateway.gateway.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final AuthenticationFilter authenticationFilter;

    public SecurityConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
        http
                .cors().disable()
                .csrf().disable()
                .authorizeExchange().anyExchange().permitAll();

        http.addFilterBefore(authenticationFilter, SecurityWebFiltersOrder.AUTHORIZATION);
        return http.build();
    }
}
