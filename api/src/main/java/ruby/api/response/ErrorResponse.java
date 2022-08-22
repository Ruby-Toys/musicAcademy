package ruby.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * {
 *     "code"       : code,
 *     "message"    : message,
 *     "validation" : {
 *          // 잘못된 필드에 대한 내용을 담아서 보내주어야 클라이언트 쪽에서 잘못된 값을 파악할 수 있다.
 *     }
 * }
 */
@Getter @Setter
public class ErrorResponse {

    private final int code;
    private final String message;
    private Map<String, String> validation;

    @Builder
    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addValidation(FieldError fieldError) {
        if (validation == null) this.validation = new HashMap<>();

        this.validation.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
