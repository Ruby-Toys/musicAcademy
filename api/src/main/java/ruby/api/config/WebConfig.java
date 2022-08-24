package ruby.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ruby.api.request.student.resolver.StudentUpdateArgumentResolver;
import ruby.api.security.UserAccountArgumentResolver;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserAccountArgumentResolver userAccountArgumentResolver;
    private final StudentUpdateArgumentResolver studentUpdateArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userAccountArgumentResolver);
        resolvers.add(studentUpdateArgumentResolver);
    }
}