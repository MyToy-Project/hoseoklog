package com.hoseoklog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.hoseoklog.domain.Post;
import com.hoseoklog.exception.PostNotFoundException;
import com.hoseoklog.repository.PostRepository;
import com.hoseoklog.request.PostCreateRequest;
import com.hoseoklog.request.PostSearchRequest;
import com.hoseoklog.request.PostUpdateRequest;
import com.hoseoklog.response.PostResponse;
import com.hoseoklog.response.PostsResponse;
import java.util.List;
import java.util.stream.IntStream;
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

    @DisplayName("존재하지 않는 단건 게시글 조회시 예외가 발생합니다.")
    @Test
    void findPost_notFoundPost() {
        // given
        Post post = Post.builder()
                .title("방금 작성한 제목입니다.")
                .content("방금 작성한 내용입니다.")
                .build();
        postRepository.save(post);

        // when & then
        assertThatThrownBy(() -> postService.findPost(post.getId() + 1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다.");
    }

    @DisplayName("1페이지 게시글 조회")
    @Test
    void findPosts() {
        // given
        List<Post> posts = IntStream.range(1, 21)
                .mapToObj(i -> Post.builder()
                        .title("foo" + i)
                        .content("bar" + i)
                        .build())
                .toList();
        postRepository.saveAll(posts);

        // when
        PostSearchRequest postSearchRequest = PostSearchRequest.builder().build();
        PostsResponse findPosts = postService.findPosts(postSearchRequest);

        // then
        assertAll(
                () -> assertThat(findPosts.posts().size()).isEqualTo(10),
                () -> assertThat(findPosts.posts().get(0).title()).isEqualTo("foo20")
        );
    }

    @DisplayName("글 제목을 수정할 수 있다.")
    @Test
    void updatePostTitle() {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // when
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("수정된 제목입니다.")
                .build();
        postService.updatePost(post.getId(), postUpdateRequest);

        // then
        Post findPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertAll(
                () -> assertThat(findPost.getTitle()).isEqualTo("수정된 제목입니다."),
                () -> assertThat(findPost.getContent()).isEqualTo("내용입니다.")
        );
    }

    @DisplayName("글 내용을 수정할 수 있다.")
    @Test
    void updatePostContent() {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // when
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .content("수정된 내용입니다.")
                .build();
        postService.updatePost(post.getId(), postUpdateRequest);

        // then
        Post findPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertAll(
                () -> assertThat(findPost.getTitle()).isEqualTo("제목입니다."),
                () -> assertThat(findPost.getContent()).isEqualTo("수정된 내용입니다.")
        );
    }

    @DisplayName("존재하지 않는 글 내용을 수정하면 예외가 발생한다.")
    @Test
    void updatePostContent_notFoundPost() {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // when & then
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .content("수정된 내용입니다.")
                .build();
        assertThatThrownBy(() -> postService.updatePost(post.getId() + 1L, postUpdateRequest))
                .isInstanceOf(PostNotFoundException.class);
    }

    @DisplayName("존재하지 않는 글을 삭제하면 예외가 발생한다.")
    @Test
    void deletePost_notFoundPost() {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // when & then
        assertThatThrownBy(() -> postService.deletePost(post.getId() + 1L))
                .isInstanceOf(PostNotFoundException.class);
    }

    @DisplayName("글을 삭제할 수 있다.")
    @Test
    void deletePost() {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // when
        postService.deletePost(post.getId());

        // then
        long count = postRepository.count();
        assertThat(count).isEqualTo(0);
    }
}