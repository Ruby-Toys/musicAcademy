package ruby.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KakaoPayProperties {


    private String host;

    private String adminKey;

    @Value("${kakaopay.host}")
    public void setHost(String host) {
        this.host = host;
    }

    @Value("${kakaopay.adminkey}")
    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }
}
