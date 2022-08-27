package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ruby.core.domain.Schedule;
import ruby.core.repository.ScheduleRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ScheduleRepository scheduleRepository;

    public void sendEmail() {
        // TODO - 메일을 보낼 때 내일 스케줄이 있는 수강생 목록을 조회한 뒤 메일을 보낸다.
        List<Schedule> schedules = scheduleRepository.findByTomorrow();

        try {
            for (Schedule schedule : schedules) {
                Context context = new Context();
                context.setVariable("studentName", schedule.getStudent().getName());
                context.setVariable("message", messageCreator(schedule));
                String htmlMessage = templateEngine.process("mail/scheduleNotice", context);

                MimeMessage mimeMessage = mailSender.createMimeMessage();

                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                mimeMessageHelper.setTo(schedule.getStudent().getEmail());
                mimeMessageHelper.setSubject("레슨 예정 알림");
                mimeMessageHelper.setText(htmlMessage, true);        // boolean 값은 메시지를 html 형태로 보낼 것인가에 대한 여부
                mailSender.send(mimeMessage);
            }
        } catch (MessagingException e) {
        }
    }

    private String messageCreator(Schedule schedule) {
        return  "내일은 " +
                schedule.getTeacher().getName() +
                " 선생님과 " +
                schedule.getStart().getHour() +
                "시" +
                (schedule.getStart().getMinute() > 0 ?
                        (" " + schedule.getStart().getMinute() + "분") : "") +
                "부터 " +
                schedule.getEnd().getHour() +
                "시" +
                (schedule.getEnd().getMinute() > 0 ?
                        (" " + schedule.getEnd().getMinute() + "분") : "") +
                "까지 레슨이 예정되어 있습니다. 원활하고 즐거운 레슨을 위해 늦지않게 준비해주세요.";
    }
}
