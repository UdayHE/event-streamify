package io.github.udayhe.controller;

import io.github.udayhe.helper.InstanceIdentificationHelper;
import io.github.udayhe.scheduler.SchedulerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author udayhegde
 */
@RequiredArgsConstructor
@Slf4j
@Controller("/scheduler")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final InstanceIdentificationHelper instanceIdentificationHelper;

    @Get("/last-execution-time")
    public HttpResponse<String> getSchedulerLastExecutionTime() {
        return HttpResponse.ok("Host:"+instanceIdentificationHelper.getIdentifier()
                + "\n" + schedulerService.getSchedulerExecutionTime());
    }
}
