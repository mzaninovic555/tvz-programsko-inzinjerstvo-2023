package hr.tvz.pios.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.pios.common.Message;
import hr.tvz.pios.common.exception.BasicResponse;
import hr.tvz.pios.config.security.jwt.PiosAuthConverter;
import hr.tvz.pios.config.security.jwt.PiosJwtDecoder;
import hr.tvz.pios.modul.user.CustomOAuth2User;
import hr.tvz.pios.modul.user.CustomOAuth2UserService;
import hr.tvz.pios.modul.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Konfiguracijska klasa za Spring Security.
 */
@Configuration
public class SecurityConfig {
  private final PiosJwtDecoder piosJwtDecoder;
  private final PiosAuthConverter piosAuthConverter;
  private final ObjectMapper mapper = new ObjectMapper();

  public SecurityConfig(PiosJwtDecoder piosJwtDecoder, PiosAuthConverter piosAuthConverter) {
    this.piosJwtDecoder = piosJwtDecoder;
    this.piosAuthConverter = piosAuthConverter;
  }

  // ovdje dodati URL koje ne treba autentificirati
  public static final String[] UNAUTHENTICATED_URLS = {
    "/api/v1/login",
    "/api/v1/register",
  };

  @Autowired
  private CustomOAuth2UserService oauthUserService;

  @Autowired
  private UserService userService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
            .requestMatchers(UNAUTHENTICATED_URLS)
            .permitAll()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .permitAll()
            .requestMatchers("/api/**")
            .authenticated()
            .anyRequest()
            .permitAll()
            .and()
            .oauth2Login()
            .loginPage("/oauth2/authorization/github")
            .userInfoEndpoint()
            .userService(oauthUserService)
            .and()
            .successHandler((request, response, authentication) -> {
              CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

              userService.processOAuthPostLogin(oauthUser.getName(), oauthUser.getEmail());
              //redirect nakon logina sa oauth2
              response.sendRedirect("/");
            });
    //TODO dodati redirect na frontend i jwt token za oauth2 usera

    http.oauth2ResourceServer()
        .jwt()
        .decoder(piosJwtDecoder)
        .jwtAuthenticationConverter(piosAuthConverter);

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.exceptionHandling()
        .accessDeniedHandler(
            (req, response, e) ->
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
        .authenticationEntryPoint(
            ((req, response, a) ->
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")));

    http.cors().and().csrf().disable();
    http.headers().frameOptions().disable();
    return http.build();
  }

  private void sendErrorResponse(HttpServletResponse response, int status, String message)
      throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(status);
    mapper.writeValue(response.getOutputStream(), new BasicResponse(Message.error(message)));
  }
}
