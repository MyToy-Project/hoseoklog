package com.hoseoklog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.hoseoklog.domain.Post;
import com.hoseoklog.repository.PostRepository;
import com.hoseoklog.request.PostCreateRequest;
import com.hoseoklog.response.PostResponse;
import com.hoseoklog.response.PostsResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @DisplayName("글 작성")
    @Test
    void writePost() {
        // given
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        postService.write(postCreateRequest);
        Post post = postRepository.findAll().get(0);

        // then
        assertAll(
                () -> assertThat(post.getTitle()).isEqualTo("제목입니다."),
                () -> assertThat(post.getContent()).isEqualTo("내용입니다.")
        );
    }

    @DisplayName("단건 게시글 조회할 수 없다면 예외가 발생합니다.")
    @Test
    void findPost_byInvalidId() {
        // given & when & then
        assertThatThrownBy(() -> postService.findPost(1111L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("단건 게시글 조회")
    @Test
    void findPost() {
        // given
        Post post = Post.builder()
                .title("방금 작성한 제목입니다.")
                .content("방금 작성한 내용입니다.")
                .build();
        postRepository.save(post);

        // when
        PostResponse findPost = postService.findPost(post.getId());

        // then
        assertAll(
                () -> assertThat(findPost.title()).isEqualTo("방금 작성한 제목입니다."),
                () -> assertThat(findPost.content()).isEqualTo("방금 작성한 내용입니다.")
        );
    }

    @DisplayName("여러개의 게시글 조회")
    @Test
    void findPosts() {
        // given
        Post post1 = Post.builder()
                .title("foo1")
                .content("bar1")
                .build();
        Post post2 = Post.builder()
                .title("foo2")
                .content("bar2")
                .build();
        List<Post> posts = List.of(post1, post2);
        postRepository.saveAll(posts);

        // when
        PostsResponse findPosts = postService.findPosts();

        // then
        assertAll(
                () -> assertThat(findPosts.posts().size()).isEqualTo(2)
        );
    }
}