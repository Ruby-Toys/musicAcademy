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

    @Scheduled(cron = "0 46 20 * * *")
    public void mailSender() {
        scheduleService.getListByTomorrow().forEach(mailService::sendMail);
    }
}
