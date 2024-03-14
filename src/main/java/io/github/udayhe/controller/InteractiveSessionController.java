package io.github.udayhe.controller;


import io.github.udayhe.request.Message;
import io.github.udayhe.service.InteractiveSessionService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller("/websocket")
public class InteractiveSessionController {

    private final InteractiveSessionService interactiveSessionService;

    @Post("/send")
    public HttpResponse<String> sendMessage(@Body Message message) {
        interactiveSessionService.sendMessage(message);
        return HttpResponse.ok();
    }
}
