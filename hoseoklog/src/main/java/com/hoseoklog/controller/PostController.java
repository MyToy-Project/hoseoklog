package com.hoseoklog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PostController {

    @PostMapping("/posts")
    public String post(@RequestBody PostCreateRequest postCreateRequest) {
        log.info("postCreateRequest = {}", postCreateRequest);
        return "Hello World!";
    }
}
