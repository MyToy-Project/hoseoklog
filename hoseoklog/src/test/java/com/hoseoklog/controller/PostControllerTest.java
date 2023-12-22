package com.hoseoklog.controller;

import static org.assertj.core.api.Assertions.assertThat;
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

    @DisplayName("글 여러개 조회")
    @Test
    void findPosts() throws Exception {
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

        // when & then
        mockMvc.perform(get("/posts")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.posts.size()").value(2),
                        jsonPath("$.posts[0].title").value("foo1"),
                        jsonPath("$.posts[0].content").value("bar1"),
                        jsonPath("$.posts[1].title").value("foo2"),
                        jsonPath("$.posts[1].content").value("bar2")
                )
                .andDo(print());
    }
}