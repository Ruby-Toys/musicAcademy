package ruby.api.response.kakao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Card {

    private String purchase_corp;
    private String purchase_corp_code;
    private String issuer_corp;
    private String issuer_corp_code;
    private String bin;
    private String card_type;
    private String install_month;
    private String approved_id;
    private String card_mid;
    private String interest_free_install;
    private String card_item_code;
}
