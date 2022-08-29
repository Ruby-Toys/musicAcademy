package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ruby.api.response.kakao.ApproveResponse;
import ruby.api.request.kakao.KakaoPayReady;
import ruby.api.response.kakao.ReadyResponse;
import ruby.api.service.KakaoPayService;
import ruby.api.service.PaymentService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/kakaoPay")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;
    private final PaymentService paymentService;

    @GetMapping("/ready")
    @ResponseBody
    public ReadyResponse ready(KakaoPayReady kakaoPayReady) {
        return kakaoPayService.payReady(kakaoPayReady.getStudentId(), kakaoPayReady.getAmount());
    }

    @GetMapping("/success")
    public String success(@RequestParam("pg_token") String pgToken) {

        // 카카오 결재 요청하기
        ApproveResponse approveResponse = kakaoPayService.payApprove(pgToken);
        paymentService.add(approveResponse);

        return "kakaoPay/successForm";
    }

    // 결제 취소시 실행 url
    @GetMapping("/cancel")
    public String cancel() {
        return "kakaoPay/cancelForm";
    }

    // 결제 실패시 실행 url
    @GetMapping("/fail")
    public String fail() {
        return "kakaoPay/failForm";
    }
}
