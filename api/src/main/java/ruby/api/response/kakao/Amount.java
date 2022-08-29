package ruby.api.response.kakao;

import lombok.Data;

@Data
public class Amount {

    private Long total;
    private Integer tax_free;
    private Integer vat;
    private Integer point;
    private Integer discount;
}
