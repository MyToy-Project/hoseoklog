package com.hoseoklog.service;

import com.hoseoklog.domain.Post;
import com.hoseoklog.repository.PostRepository;
import com.hoseoklog.request.PostCreateRequest;
import com.hoseoklog.response.PostCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(final PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostCreateResponse write(final PostCreateRequest postCreateRequest) {
        Post post = Post.builder()
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .build();

        Post savedPost = postRepository.save(post);
        return new PostCreateResponse(savedPost.getId());
    }
}
