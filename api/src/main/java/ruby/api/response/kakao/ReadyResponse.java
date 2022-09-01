package ruby.api.response.kakao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReadyResponse {
    private String tid;
    private String next_redirect_pc_url;
    private Date created_at;
}
