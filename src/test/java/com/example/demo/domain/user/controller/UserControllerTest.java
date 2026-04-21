package com.example.demo.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.global.Exception.CustomException;
import com.example.demo.global.Exception.ErrorCode;
import com.example.demo.global.Exception.GlobalExceptionHandler;
import com.example.demo.global.config.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import com.example.demo.domain.user.dto.UserRequest;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.service.UserService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("UserController 테스트")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  // ───────────────────────────────────────────────
  // 공통 픽스처
  // ───────────────────────────────────────────────

  private UserRequest.Create buildRequest() {
    return new UserRequest.Create(
        "홍길동",
        "hong123",
        "password123!",
        "01012345678",
        "USER"
    );
  }

  private User buildUser() {
    return User.builder()
        .name("홍길동")
        .loginId("hong123")
        .password("$2a$encoded")
        .phone("01012345678")
        .role("USER")
        .build();
  }

  // ─────────────────────────────────────────────
  // 테스트 케이스
  // ─────────────────────────────────────────────

  @Nested
  @DisplayName("create user test")
  class create_user {

    private final String createUserUri = "/api/users/";
    // ───────────────────────────────────────────────
    // 성공 케이스
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/users/ - 회원가입 성공 시 201 반환")
    void createUser_success() throws Exception {
      // given
      given(userService.createUser(any(UserRequest.Create.class)))
          .willReturn(buildUser());

      // when & then
      mockMvc.perform(post(createUserUri)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(buildRequest())))
          .andExpect(status().isCreated())                         // 201
          .andExpect(jsonPath("$.data.loginId").value("hong123"))
          .andExpect(jsonPath("$.data.name").value("홍길동"))
          .andExpect(jsonPath("$.data.password").doesNotExist())
          .andDo(print());
    }

    // ───────────────────────────────────────────────
    // 실패 케이스 - 비즈니스 예외
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/users/ - 중복 loginId 시 409 반환")
    void createUser_fail_duplicateLoginId() throws Exception {
      // given
      given(userService.createUser(any(UserRequest.Create.class)))
          .willThrow(new CustomException(ErrorCode.DUPLICATE_LOGIN_ID));

      // when & then
      mockMvc.perform(post(createUserUri)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(buildRequest())))
          .andExpect(status().isConflict())                        // 409
          .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_LOGIN_ID.getMessage()))
          .andDo(print());
    }

    // ───────────────────────────────────────────────
    // 실패 케이스 - @Valid 입력값 검증
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/users/ - name 공백 시 400 반환")
    void createUser_fail_blankName() throws Exception {
      // given - name만 공백으로
      UserRequest.Create invalidRequest = new UserRequest.Create(
          "",           // @NotBlank 위반
          "hong123",
          "password123!",
          "01012345678",
          "USER"
      );

      // when & then
      mockMvc.perform(post(createUserUri)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest())                      // 400
          .andExpect(jsonPath("$.message").value("사용자 이름은 필수입니다."))
          .andDo(print());
    }

    @Test
    @DisplayName("POST /api/users/ - 잘못된 전화번호 형식 시 400 반환")
    void createUser_fail_invalidPhone() throws Exception {
      // given - 하이픈 포함된 잘못된 형식
      UserRequest.Create invalidRequest = new UserRequest.Create(
          "홍길동",
          "hong123",
          "password123!",
          "010-1234-5678",  // @Pattern 위반
          "USER"
      );

      // when & then
      mockMvc.perform(post(createUserUri)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("하이픈 없이 숫자만 입력해주세요"))
          .andDo(print());
    }

    @Test
    @DisplayName("POST /api/users/ - 비밀번호 8자 미만 시 400 반환")
    void createUser_fail_shortPassword() throws Exception {
      // given
      UserRequest.Create invalidRequest = new UserRequest.Create(
          "홍길동",
          "hong123",
          "pw1!",          // @Size(min=8) 위반
          "01012345678",
          "USER"
      );

      // when & then
      mockMvc.perform(post(createUserUri)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidRequest)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message").value("비밀번호는 최소 8자 이상, 최대 20자 이하입니다.")).andDo(print());
    }
  }

  @Nested
  @DisplayName("search user test")
  class search_user {

    private final String searchUri = "/api/users/";

    // ───────────────────────────────────────────────
    // 성공 케이스
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/users - 조건 없이 전체 조회 성공")
    void searchUsers_noCondition_success() throws Exception {
      // given
      List<User> users = List.of(buildUser(), buildUser()); // 전체 2명
      given(userService.searchUsers(any(UserRequest.SearchCondition.class), any()))
          .willReturn(new PageImpl<>(users, PageRequest.of(0, 10), 2));

      // when & then
      mockMvc.perform(get(searchUri)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())                               // 200
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.totalElements").value(2))
          .andExpect(jsonPath("$.data.content").isArray())          // content가 배열인지
          .andDo(print());
    }

    @Test
    @DisplayName("GET /api/users?name=홍 - 이름 필터 조회 성공")
    void searchUsers_withNameFilter_success() throws Exception {
      // given
      List<User> users = List.of(buildUser()); // 이름 '홍' 검색 → 1명 매칭
      given(userService.searchUsers(any(UserRequest.SearchCondition.class), any()))
          .willReturn(new PageImpl<>(users, PageRequest.of(0, 10), 1));

      // when & then
      mockMvc.perform(get(searchUri)
              .param("name", "홍")
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())                               // 200
          .andExpect(jsonPath("$.data.totalElements").value(1))
          .andExpect(jsonPath("$.data.content[0].name").value("홍길동"))
          .andDo(print());
    }

    // ───────────────────────────────────────────────
    // 실패(엣지) 케이스
    // ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/users?name=없음 - 결과 없을 시 빈 배열 반환")
    void searchUsers_noResult_emptyPage() throws Exception {
      // given
      given(userService.searchUsers(any(UserRequest.SearchCondition.class), any()))
          .willReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));
      // 조건에 맞는 데이터가 없으면 빈 페이지 반환 — 404가 아닌 200이어야 함

      // when & then
      mockMvc.perform(get(searchUri)
              .param("name", "없음")
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())                               // 200 (빈 결과도 정상 응답)
          .andExpect(jsonPath("$.data.content").isEmpty())
          .andExpect(jsonPath("$.data.totalElements").value(0))
          .andDo(print());
    }
  }
}