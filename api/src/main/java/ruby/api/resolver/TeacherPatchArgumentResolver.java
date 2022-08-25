package ruby.api.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ruby.api.request.teacher.TeacherPatch;
import ruby.core.domain.enums.Course;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class TeacherPatchArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return TeacherPatch.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return TeacherPatch.builder()
                .name((String)request.getAttribute("name"))
                .email((String)request.getAttribute("email"))
                .phoneNumber((String)request.getAttribute("phoneNumber"))
                .course(Course.valueOf((String)request.getAttribute("course")))
                .build();
    }
}
