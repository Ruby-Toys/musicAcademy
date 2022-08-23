package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.api.response.schedule.SchedulesResponse;
import ruby.api.service.ScheduleService;
import ruby.core.domain.Schedule;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public SchedulesResponse getList(@Valid ScheduleSearch search) {
        List<Schedule> schedules = scheduleService.getList(search);
        return new SchedulesResponse(schedules);
    }

}
