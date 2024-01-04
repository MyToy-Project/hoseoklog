package com.hoseoklog.controller;

import static com.util.ApiDocumentUtils.getDocumentRequest;
import static com.util.ApiDocumentUtils.getDocumentResponse;
import static com.util.DocumentFormatGenerator.getAttribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoseoklog.domain.Post;
import com.hoseoklog.repository.PostRepository;
import com.hoseoklog.request.PostCreateRequest;
import com.hoseoklog.request.PostUpdateRequest;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .build();
        postRepository.deleteAll();
    }

    @DisplayName("게시글을 정상 작성하면 200이 반환 됩니다.")
    @Test
    void savePost_toDatabase() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/posts")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));
        Post post = postRepository.findAll().get(0);

        // then
        result.andExpectAll(
                        status().isOk(),
                        jsonPath("$.savedId").value(post.getId()))
                .andDo(
                        document(
                                "post/create/success",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목")
                                                .attributes(getAttribute("constraints", "제목은 반드시 입력해야 합니다.")),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                                                .attributes(getAttribute("constraints", "내용은 반드시 입력해야 합니다."))
                                ),
                                responseFields(
                                        fieldWithPath("savedId").type(JsonFieldType.NUMBER).description("저장된 게시글 id")
                                )
                        )
                );
    }

    @DisplayName("게시글에 제목, 내용을 모두 입력하지 않는다면 400을 반환합니다.")
    @Test
    void createPost_exception_emptyTitleAndContent() throws Exception {
        // given & when & then
        PostCreateRequest request = PostCreateRequest.builder()
                .build();
        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.code").value("400"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.title").value("제목을 입력해주세요"))
                .andDo(
                        document(
                                "post/create/fail/emptyTitleAndContent",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.STRING)
                                                .description("HTTP Response Status"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("오류 대표 메시지"),
                                        fieldWithPath("validation").type(JsonFieldType.OBJECT)
                                                .description("오류가 있는 필드 및 설명을 담는 객체"),
                                        fieldWithPath("validation.title").type(JsonFieldType.STRING)
                                                .description("게시글 제목 오류 메시지"),
                                        fieldWithPath("validation.content").type(JsonFieldType.STRING)
                                                .description("게시글 내용 오류 메시지")
                                )
                        )
                );
    }

    @DisplayName("게시글에 제목을 입력하지 않는다면 400을 반환합니다.")
    @Test
    void createPost_exception_emptyTitle() throws Exception {
        // given & when & then
        PostCreateRequest request = PostCreateRequest.builder()
                .content("내용입니다.")
                .build();
        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.code").value("400"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.title").value("제목을 입력해주세요"))
                .andDo(
                        document(
                                "post/create/fail/emptyTitle",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
                );
    }

    @DisplayName("게시글에 내용을 입력하지 않는다면 400을 반환합니다.")
    @Test
    void createPost_exception_emptyContent() throws Exception {
        // given & when & then
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목입니다.")
                .build();
        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.code").value("400"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validation.content").value("본문을 입력해주세요"))
                .andDo(
                        document(
                                "post/create/fail/emptyContent",
                                getDocumentRequest(),
                                getDocumentResponse()
                        )
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
                .andExpect(jsonPath("$.content").value("bar"));
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
                );
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
                );
    }

    @DisplayName("작성한 게시글의 제목을 수정할 수 있다.")
    @Test
    void updatePostTitle() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // when
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("새로운 제목입니다.")
                .build();
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        Post findPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + post.getId()));
        assertAll(
                () -> assertThat(findPost.getTitle()).isEqualTo("새로운 제목입니다."),
                () -> assertThat(findPost.getContent()).isEqualTo("내용입니다.")
        );
    }

    @DisplayName("작성한 게시글의 내용을 수정할 수 있다.")
    @Test
    void updatePostContent() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // when
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .content("새로운 내용입니다.")
                .build();
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .content(objectMapper.writeValueAsString(postUpdateRequest))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        Post findPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + post.getId()));
        assertAll(
                () -> assertThat(findPost.getTitle()).isEqualTo("제목입니다."),
                () -> assertThat(findPost.getContent()).isEqualTo("새로운 내용입니다.")
        );
    }

    @DisplayName("게시글을 삭제할 수 있다.")
    @Test
    void deletePost() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // then
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        long count = postRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @DisplayName("존재하지 않는 게시글을 조회하면 404가 반환됩니다.")
    @Test
    void findNotExistsPost() throws Exception {
        // expected
        mockMvc.perform(get("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("존재하지 않는 게시글을 수정하면 404가 반환됩니다.")
    @Test
    void updateNotExistsPost() throws Exception {
        // given
        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("제목")
                .content("내용")
                .build();

        // expected
        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("존재하지 않는 게시글을 삭제하면 404가 반환됩니다.")
    @Test
    void deleteNotExistsPost() throws Exception {
        // expected
        mockMvc.perform(delete("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("게시글 작성시 제목에 바보라는 키워드가 추가되면 400이 반환됩니다.")
    @Test
    void writePost_withInvalidKeyword() throws Exception {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("제목 바보입니다.")
                .content("내용입니다.")
                .build();

        // expected
        mockMvc.perform(post("/posts")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}