package com.example.demo.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.example.demo.domain.user.dto.UserRequest;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.Exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)  // Spring 컨텍스트 없이 Mockito만 사용
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @Nested
  @DisplayName("회원가입 테스트")
  class CreateUser {


    // ───────────────────────────────────────────
    // 공통 픽스처 (테스트마다 반복되는 데이터)
    // ───────────────────────────────────────────
    private UserRequest.Create buildRequest() {
      return new UserRequest.Create(
          "홍길동",
          "hong123",
          "password123!",
          "01012345678",
          "USER"
      );
    }

    // ───────────────────────────────────────────
    // 성공 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("회원가입 성공 - 신규 아이디, 비밀번호 암호화 저장")
    void createUser_success() {
      // given
      UserRequest.Create request = buildRequest();

      given(userRepository.existsByLoginId("hong123")).willReturn(false);
      given(passwordEncoder.encode("password123!")).willReturn("$2a$encoded");

      User savedUser = User.builder()
          .name("홍길동")
          .loginId("hong123")
          .password("$2a$encoded")
          .phone("01012345678")
          .role("USER")
          .build();
      given(userRepository.save(any(User.class))).willReturn(savedUser);

      // when
      User result = userService.createUser(request);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getLoginId()).isEqualTo("hong123");
      assertThat(result.getPassword()).isEqualTo("$2a$encoded");  // 암호화 됐는지 확인
      assertThat(result.getPassword()).isNotEqualTo("password123!");  // 평문이 아닌지 확인

      // 실제로 encode()와 save()가 한 번씩 호출됐는지 검증
      then(userRepository).should(times(1)).existsByLoginId("hong123");
      then(passwordEncoder).should(times(1)).encode("password123!");
      then(userRepository).should(times(1)).save(any(User.class));
    }

    // ───────────────────────────────────────────
    // 실패 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("회원가입 실패 - 중복 loginId 예외 발생")
    void createUser_fail_duplicateLoginId() {
      // given
      UserRequest.Create request = buildRequest();

      // 이미 존재하는 아이디라고 가정
      given(userRepository.existsByLoginId("hong123")).willReturn(true);

      // when & then
      assertThatThrownBy(() -> userService.createUser(request))
          .isInstanceOf(CustomException.class)
          .hasMessageContaining("이미 사용 중인 아이디입니다");

      // 중복이면 save()는 절대 호출되면 안 됨
      then(userRepository).should(never()).save(any(User.class));
      then(passwordEncoder).should(never()).encode(anyString());
    }

    @Test
    @DisplayName("회원가입 실패 - role이 null이면 USER로 기본값 설정")
    void createUser_roleNull_defaultsToUser() {
      // given
      UserRequest.Create request = new UserRequest.Create(
          "홍길동", "hong123", "password123!", "01012345678", null  // role = null
      );

      given(userRepository.existsByLoginId("hong123")).willReturn(false);
      given(passwordEncoder.encode(anyString())).willReturn("$2a$encoded");
      given(userRepository.save(any(User.class))).willAnswer(
          invocation -> invocation.getArgument(0));
      // save()에 넘긴 User 객체를 그대로 반환하도록 설정

      // when
      User result = userService.createUser(request);

      // then
      assertThat(result.getRole()).isEqualTo("USER");
    }
  }
}
