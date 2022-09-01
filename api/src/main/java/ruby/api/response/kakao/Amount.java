package ruby.api.response.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Amount {

    private Long total;
    private Integer tax_free;
    private Integer vat;
    private Integer point;
    private Integer discount;
}
