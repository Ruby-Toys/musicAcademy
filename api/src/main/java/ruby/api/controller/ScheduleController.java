package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ruby.api.exception.schedule.ScheduleWrongDateException;
import ruby.api.request.schedule.SchedulePatch;
import ruby.api.request.schedule.SchedulePost;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.api.response.schedule.ScheduleResponse;
import ruby.api.response.schedule.SchedulesResponse;
import ruby.api.service.ScheduleService;
import ruby.api.utils.DateUtils;
import ruby.core.domain.Schedule;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ScheduleResponse post(@RequestBody @Valid SchedulePost schedulePost) {
        if (!DateUtils.compareDateString(schedulePost.getStart(), schedulePost.getEnd())) {
            throw new ScheduleWrongDateException();
        }

        Schedule schedule = scheduleService.add(schedulePost);
        return new ScheduleResponse(schedule);
    }

    @GetMapping
    public SchedulesResponse getList(@Valid ScheduleSearch search) {
        List<Schedule> schedules = scheduleService.getList(search);
        return new SchedulesResponse(schedules);
    }

    @PatchMapping("/{id}")
    public ScheduleResponse patch(@PathVariable Long id, @RequestBody @Valid SchedulePatch schedulePatch) {
        if (!DateUtils.compareDateString(schedulePatch.getStart(), schedulePatch.getEnd())) {
            throw new ScheduleWrongDateException();
        }

        Schedule schedule = scheduleService.update(id, schedulePatch);
        return new ScheduleResponse(schedule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        scheduleService.delete(id);
    }
}
