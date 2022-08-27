package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ruby.api.service.MailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {
    private final MailService mailService;

    @GetMapping
    public void send() {
        // 배치를 통해 일정 시간마다 다음날에 스케줄이 있는 수강생의 목록을 찾은 뒤 메일을 전송한다.
        mailService.sendEmail();
    }
}
