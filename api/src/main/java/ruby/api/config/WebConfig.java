package ruby.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ruby.api.resolver.TeacherPatchArgumentResolver;
import ruby.api.resolver.StudentPostArgumentResolver;
import ruby.api.resolver.StudentPatchArgumentResolver;
import ruby.api.resolver.TeacherPostArgumentResolver;
import ruby.api.security.UserAccountArgumentResolver;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserAccountArgumentResolver userAccountArgumentResolver;
    private final StudentPatchArgumentResolver studentPatchArgumentResolver;
    private final StudentPostArgumentResolver studentPostArgumentResolver;
    private final TeacherPatchArgumentResolver teacherPatchArgumentResolver;
    private final TeacherPostArgumentResolver teacherPostArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userAccountArgumentResolver);
        resolvers.add(studentPatchArgumentResolver);
        resolvers.add(studentPostArgumentResolver);
        resolvers.add(teacherPatchArgumentResolver);
        resolvers.add(teacherPostArgumentResolver);
    }
}