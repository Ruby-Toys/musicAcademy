package ruby.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ruby.api.config.KakaoPayProperties;
import ruby.api.exception.student.StudentNotFoundException;
import ruby.api.response.kakao.ApproveResponse;
import ruby.api.response.kakao.ReadyResponse;
import ruby.core.domain.Student;
import ruby.core.repository.StudentRepository;

import javax.servlet.http.HttpSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

//    @Value("${kakaopay.host}")
//    private String host;
//    @Value("${kakaopay.adminkey}")
//    private String adminKey;

    private final KakaoPayProperties kakaoPayProperties;
    private final HttpSession httpSession;

    private final StudentRepository studentRepository;

    public ReadyResponse payReady(Long id, Long amount) {
        Student student = studentRepository.findById(id)
                .orElseThrow(StudentNotFoundException::new);

        String partner_order_id = student.getName() + "_" + student.getCourse();
        String partner_user_id = String.valueOf(id);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", partner_order_id);
        params.add("partner_user_id", partner_user_id);
        params.add("item_name", student.getCourse().name() + "_" + student.getGrade().name());
        params.add("quantity", "1");
        params.add("total_amount", String.valueOf(amount));
        params.add("tax_free_amount", "0");
        params.add("approval_url", "http://localhost:8080/kakaoPay/success"); // 결제승인시 넘어갈 url
        params.add("cancel_url", "http://localhost:8080/kakaoPay/cancel"); // 결제취소시 넘어갈 url
        params.add("fail_url", "http://localhost:8080/kakaoPay/fail"); // 결제 실패시 넘어갈 url

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, this.getHeaders());

        RestTemplate template = new RestTemplate();
        String url = kakaoPayProperties.getHost() + "/v1/payment/ready";
        ReadyResponse readyResponse = template.postForObject(url, requestEntity, ReadyResponse.class);

        httpSession.setAttribute("partner_order_id", partner_order_id);
        httpSession.setAttribute("partner_user_id", partner_user_id);
        httpSession.setAttribute("tid", readyResponse.getTid());
        httpSession.setAttribute("amount", amount);

        return readyResponse;
    }

    public ApproveResponse payApprove(String pgToken) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", (String) httpSession.getAttribute("tid"));
        params.add("partner_order_id", (String) httpSession.getAttribute("partner_order_id"));
        params.add("partner_user_id", (String) httpSession.getAttribute("partner_user_id"));
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, this.getHeaders());

        RestTemplate template = new RestTemplate();
        String url = kakaoPayProperties.getHost() + "/v1/payment/approve";
        return template.postForObject(url, requestEntity, ApproveResponse.class);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoPayProperties.getAdminKey());
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        return headers;
    }
}
