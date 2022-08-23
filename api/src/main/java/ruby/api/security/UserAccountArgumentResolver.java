package ruby.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class UserAccountArgumentResolver implements HandlerMethodArgumentResolver {
    private final HttpSession httpSession;

    /**
     * 컨트롤러 메서드의 특정 파라미터를 지원하는지 판단
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 해당 어노테이션이 붙은 파라미터가 있는지 확인
        boolean isLoginAccountAnnotation = parameter.getParameterAnnotation(LoginAccount.class) != null;

        // UserAccount 타입의 파라미터가 있는지 확인
        boolean isAccountClass = UserAccount.class.equals(parameter.getParameterType());

        // @LoginAccount 가 붙어있고 타입이 UserAccount 인 파라미터가 있는 경우 true
        return isLoginAccountAnnotation && isAccountClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("account");
    }
}
