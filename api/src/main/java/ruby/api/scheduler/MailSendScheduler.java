package ruby.api.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ruby.api.service.MailService;
import ruby.api.service.ScheduleService;

@Component
@RequiredArgsConstructor
public class MailSendScheduler {

    private final ScheduleService scheduleService;
    private final MailService mailService;

    @Scheduled(cron = "0 0 18 * * *")   // 매일 오후 6시에 다음날 스케줄이 있는 수강생에게 메일 전송
    public void mailSender() {
        scheduleService.getListByTomorrow().forEach(mailService::sendMail);
    }
}
