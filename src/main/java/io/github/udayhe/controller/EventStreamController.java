package io.github.udayhe.controller;


import io.github.udayhe.request.Message;
import io.github.udayhe.response.ChannelStatusResponse;
import io.github.udayhe.service.EventStreamService;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.sse.Event;
import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static io.micronaut.http.HttpResponse.ok;

@Controller("/sse")
@RequiredArgsConstructor
@Slf4j
public class EventStreamController {

    private final EventStreamService eventStreamService;

    @Get("/register/{channel}")
    @Consumes(MediaType.ALL)
    @Produces(MediaType.TEXT_EVENT_STREAM)
    public HttpResponse<Flowable<Event<Message>>> register(String channel) {
        Flowable<Event<Message>> eventStream = eventStreamService.register(channel).map(Event::of);

        return ok(eventStream)
                .header("X-Accel-Buffering", "no")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header(HttpHeaders.CONNECTION, "keep-alive")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM);
    }


    @Get("/v1/register/{channel}")
    @Consumes(MediaType.ALL)
    @Produces(MediaType.TEXT_EVENT_STREAM)
    Flowable<Event<Message>> registerV1(@PathVariable String channel) {
        log.info("In SSE register. channel:{}", channel);
        return eventStreamService.register(channel).map(Event::of);
    }


    @Post("/emit-event/{channel}")
    public void emitEvent(@PathVariable String channel,
                          @Body Map<String, Object> payload) {
        log.info("In SSE emit-event. channel:{}, payload:{}", channel, payload);
        eventStreamService.emitEvent(channel, payload);
    }

    @Get("/status/{channel}")
    public HttpResponse<ChannelStatusResponse> channelStatus(@PathVariable String channel) {
        log.info("In SSE channelStatus. channel:{}", channel);
        return ok(eventStreamService.channelStatus(channel));
    }

    @Get("/heartbeat/{channel}")
    public HttpResponse<Void> heartbeat(@PathVariable String channel) {
        log.info("In SSE heartbeat. channel:{}", channel);
        eventStreamService.heartBeat(channel);
        return ok();
    }

    @Get("/v2/status/{channel}")
    public HttpResponse<ChannelStatusResponse> channelStatusV2(@PathVariable String channel) {
        log.info("In SSE V2 channelStatus. channel:{}", channel);
        return ok(eventStreamService.channelStatusV2(channel));
    }

    @Get("/connections/count")
    public HttpResponse<Map<String, Number>> getCount() {
        return ok(eventStreamService.getCount());
    }



}
