package ruby.api.request.payment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentSearch {

    private String word;
    private int page;
    public static int PAGE_SIZE = 15;
}
