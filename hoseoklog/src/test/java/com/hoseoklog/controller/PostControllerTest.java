package com.hoseoklog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoseoklog.domain.Post;
import com.hoseoklog.repository.PostRepository;
import com.hoseoklog.request.PostCreateRequest;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
    }

    @DisplayName("/posts 요청시 title값은 필수다")
    @Test
    void createPost_exception_emptyTitle() throws Exception {
        // given & when & then
        PostCreateRequest request = PostCreateRequest.builder()
                .content("내용")
                .build();
        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요"))
                .andDo(print());
    }

    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    @Test
    void savePost_toDatabase() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // when
        Post post = postRepository.findAll().get(0);

        // then
        assertAll(
                () -> assertThat(post.getTitle()).isEqualTo("제목입니다."),
                () -> assertThat(post.getContent()).isEqualTo("내용입니다.")
        );
    }

    @DisplayName("글 단건 조회")
    @Test
    void findOnePost() throws Exception {
        // given
        Post post = Post.builder()
                .title("12345678901234567890")
                .content("bar")
                .build();
        postRepository.save(post);

        // when & then
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("12345678901234567890"))
                .andExpect(jsonPath("$.content").value("bar"))
                .andDo(print());
    }

    @DisplayName("글 1페이지 조회")
    @Test
    void findPosts() throws Exception {
        // given
        List<Post> posts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("foo" + i)
                        .content("bar" + i)
                        .build())
                .toList();
        postRepository.saveAll(posts);

        // when & then
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.posts.size()", is(10)),
                        jsonPath("$.posts[0].title").value("foo30"),
                        jsonPath("$.posts[0].content").value("bar30"),
                        jsonPath("$.posts[4].title").value("foo26"),
                        jsonPath("$.posts[4].content").value("bar26")
                )
                .andDo(print());
    }

    @DisplayName("페이지를 0으로 조회해도 첫 페이지를 조회한다")
    @Test
    void findPosts_withZeroPageNumber() throws Exception {
        // given
        List<Post> posts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("foo" + i)
                        .content("bar" + i)
                        .build())
                .toList();
        postRepository.saveAll(posts);

        // when & then
        mockMvc.perform(get("/posts?page=0&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.posts.size()", is(10)),
                        jsonPath("$.posts[0].title").value("foo30"),
                        jsonPath("$.posts[0].content").value("bar30"),
                        jsonPath("$.posts[4].title").value("foo26"),
                        jsonPath("$.posts[4].content").value("bar26")
                )
                .andDo(print());
    }
}