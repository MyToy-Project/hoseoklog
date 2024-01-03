package com.hoseoklog.service;

import com.hoseoklog.domain.Post;
import com.hoseoklog.domain.PostEditor;
import com.hoseoklog.exception.PostNotFoundException;
import com.hoseoklog.repository.PostRepository;
import com.hoseoklog.request.PostCreateRequest;
import com.hoseoklog.request.PostSearchRequest;
import com.hoseoklog.request.PostUpdateRequest;
import com.hoseoklog.response.PostCreateResponse;
import com.hoseoklog.response.PostResponse;
import com.hoseoklog.response.PostsResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(PostNotFoundException::new);

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

    @Transactional
    public void updatePost(final Long id, PostUpdateRequest postUpdateRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        PostEditor postEditor = post.toEditor()
                .title(postUpdateRequest.title())
                .content(postUpdateRequest.content())
                .build();

        post.updatePost(postEditor);
    }

    public void deletePost(final Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        postRepository.delete(post);
    }
}
