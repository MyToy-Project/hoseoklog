package com.hoseoklog.controller;

import com.hoseoklog.request.PostCreateRequest;
import com.hoseoklog.response.PostCreateResponse;
import com.hoseoklog.response.PostResponse;
import com.hoseoklog.response.PostsResponse;
import com.hoseoklog.service.PostService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PostController {

    private final PostService postService;

    public PostController(final PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<PostCreateResponse> writePost(@RequestBody @Valid PostCreateRequest request) {
        PostCreateResponse response = postService.write(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> findPost(@PathVariable Long postId) {
        PostResponse postResponse = postService.findPost(postId);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/posts")
    public ResponseEntity<PostsResponse> findPosts() {
        PostsResponse postsResponse = postService.findPosts();
        return ResponseEntity.ok(postsResponse);
    }
}
