package com.hoseoklog.service;

import com.hoseoklog.domain.Post;
import com.hoseoklog.repository.PostRepository;
import com.hoseoklog.request.PostCreateRequest;
import com.hoseoklog.request.PostSearchRequest;
import com.hoseoklog.response.PostCreateResponse;
import com.hoseoklog.response.PostResponse;
import com.hoseoklog.response.PostsResponse;
import java.util.List;
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
                .title(postCreateRequest.title())
                .content(postCreateRequest.content())
                .build();

        Post savedPost = postRepository.save(post);
        return new PostCreateResponse(savedPost.getId());
    }

    public PostResponse findPost(final Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        return PostResponse.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public PostsResponse findPosts(final PostSearchRequest postSearchRequest) {
        List<Post> posts = postRepository.findPosts(postSearchRequest)
                .stream()
                .toList();

        return PostsResponse.from(posts);
    }
}
