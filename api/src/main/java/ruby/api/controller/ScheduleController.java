package ruby.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ruby.api.request.schedule.SchedulePost;
import ruby.api.request.schedule.ScheduleSearch;
import ruby.api.response.schedule.ScheduleResponse;
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

    @PostMapping
    public ScheduleResponse post(@RequestBody @Valid SchedulePost schedulePost) {
        // todo - 등록 테스트 필요
        Schedule schedule = scheduleService.add(schedulePost);
        return new ScheduleResponse(schedule);
    }

    @GetMapping
    public SchedulesResponse getList(@Valid ScheduleSearch search) {
        List<Schedule> schedules = scheduleService.getList(search);
        return new SchedulesResponse(schedules);
    }

    @PatchMapping("/{id}")
    public void patch(@PathVariable Long id) {
        // todo - 시간 및 선생님, 스케쥴 상태 변경 가능하게 수정 및 테스트 진행

        scheduleService.update(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        scheduleService.delete(id);
    }
}
