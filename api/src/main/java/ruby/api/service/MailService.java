package ruby.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ruby.core.domain.Schedule;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendMail(Schedule schedule) {
        Context context = new Context();
        context.setVariable("studentName", schedule.getStudent().getName());
        context.setVariable("message", getMessage(schedule));
        String htmlMessage = templateEngine.process("mail/scheduleNotice", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(schedule.getStudent().getEmail());
            mimeMessageHelper.setSubject("레슨 예정 알림");
            mimeMessageHelper.setText(htmlMessage, true);        // boolean 값은 메시지를 html 형태로 보낼 것인가에 대한 여부
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
        }

    }

    private String getMessage(Schedule schedule) {
        LocalDateTime start = schedule.getStart();
        LocalDateTime end = schedule.getEnd();

        return  "내일은 " +
                schedule.getTeacher().getName() +
                " 선생님과 " +
                start.getHour() +
                "시" +
                (start.getMinute() > 0 ?
                        (" " + start.getMinute() + "분") : "") +
                "부터 " +
                end.getHour() +
                "시" +
                (end.getMinute() > 0 ?
                        (" " + end.getMinute() + "분") : "") +
                "까지 레슨이 예정되어 있습니다. 원활하고 즐거운 레슨을 위해 늦지않게 준비해주세요.";
    }
}
