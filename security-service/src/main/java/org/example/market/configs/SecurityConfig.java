package org.example.market.configs;

import org.example.market.filters.JwtAuthenticationFilter;
import org.example.market.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserService userService;
@Autowired
  public SecurityConfig(final JwtAuthenticationFilter jwtAuthenticationFilter, final UserService userService) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.userService = userService;
}


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(request -> {
              CorsConfiguration corsConfiguration = new CorsConfiguration();
              corsConfiguration.setAllowCredentials(true);
              corsConfiguration.setAllowedOriginPatterns((List.of("*")));
              corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
              corsConfiguration.setAllowedHeaders(List.of("*"));
              return corsConfiguration;
            }))
            .authorizeHttpRequests(request -> request
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/auth/swagger-ui/**", "/auth/swagger-resources/*", "/auth/v3/api-docs/**").permitAll()
                    .requestMatchers("/").hasRole("USER")
                    .requestMatchers("/admin**").hasRole("ADMIN")
                    .anyRequest().authenticated())
                    .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();

  }




@Bean
    public AuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
      authenticationProvider.setUserDetailsService(userService.getUserDetailsService());
      authenticationProvider.setPasswordEncoder(passwordEncoder());
      return authenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
          return config.getAuthenticationManager();
  }


@Bean
public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
}

}
