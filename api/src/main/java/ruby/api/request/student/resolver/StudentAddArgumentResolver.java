package ruby.api.request.student.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ruby.api.request.student.StudentAdd;
import ruby.api.request.student.StudentUpdate;
import ruby.core.domain.enums.Course;
import ruby.core.domain.enums.Grade;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class StudentAddArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return StudentAdd.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return StudentUpdate.builder()
                .name((String)request.getAttribute("name"))
                .email((String)request.getAttribute("email"))
                .phoneNumber((String)request.getAttribute("phoneNumber"))
                .course(Course.valueOf((String)request.getAttribute("course")))
                .grade(Grade.valueOf((String)request.getAttribute("grade")))
                .memo((String)request.getAttribute("memo"))
                .build();
    }
}
