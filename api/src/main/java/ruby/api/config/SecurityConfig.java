package ruby.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
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
                .mvcMatchers("/signUp", "/login", "/logout", "/loginCheck").permitAll()
                .antMatchers("/students/**").hasRole("MANAGER")
                .antMatchers("/teachers/**").hasRole("MANAGER")
                .antMatchers("/schedules/**").hasRole("MANAGER")
                .antMatchers("/payments/**").hasRole("MANAGER")
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());


        // add(get, post), {boardId}/edit (get)

        /** 로그인 처리 */
//        http.formLogin().disable();

        /** 로그아웃 처리 */
        http.logout()
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(logoutSuccessHandler);

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
     * 권한 계층 설정
     * @return
     */
    private DefaultWebSecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MANAGER");

        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);

        return expressionHandler;
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
