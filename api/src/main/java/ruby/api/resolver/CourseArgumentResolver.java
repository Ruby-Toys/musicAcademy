package ruby.api.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.core.domain.enums.Course;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
public class CourseArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 컨트롤러 메서드의 특정 파라미터를 지원하는지 판단
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Course.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return Course.valueOf((String)request.getAttribute("course"));
    }
}
