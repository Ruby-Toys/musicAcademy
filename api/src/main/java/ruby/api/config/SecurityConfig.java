package ruby.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .mvcMatchers("/docs/index.html");   // doc 문서를 제공하기 위해 해당 페이지는 허용
    }

    /**
     * 시큐리티 인증 제외 url 설정
     * @return
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors().configurationSource(corsConfigurationSource());
        http.csrf().disable();

        /** 페이지 권한 설정 */
        http
                .authorizeRequests()
                .mvcMatchers("/login", "/loginCheck").permitAll()
                .mvcMatchers("/signUp").hasRole("ADMIN")
                .antMatchers("/accounts/**").hasRole("ADMIN")
                .antMatchers("/students/**").hasRole("MANAGER")
                .antMatchers("/teachers/**").hasRole("MANAGER")
                .antMatchers("/schedules/**").hasRole("MANAGER")
                .antMatchers("/payments/**").hasRole("MANAGER")
                .antMatchers("/kakaoPay/**").hasRole("MANAGER")
                .anyRequest().authenticated();


        // add(get, post), {boardId}/edit (get)

        /** 로그인 처리 */
        http.formLogin().disable();

        /** 로그아웃 처리 */
        http.logout()
                .deleteCookies("JSESSIONID");

        return http.build();
    }

    /**
     * PasswordEncoder Bean 등록
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /**
     * CORS 설정
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("http://localhost:5173/**");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
