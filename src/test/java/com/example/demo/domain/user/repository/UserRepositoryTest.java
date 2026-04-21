package com.example.demo.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import com.example.demo.domain.user.dto.UserRequest.SearchCondition;
import com.example.demo.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("UserRepository 동적 쿼리 테스트 - 실제 MariaDB")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EntityManager em;

  // ───────────────────────────────────────────
  // 공통 픽스처 (테스트마다 반복되는 데이터)
  // ───────────────────────────────────────────

  @BeforeEach
  void setUp() {
    em.createQuery("delete from User").executeUpdate();
    em.flush();

    userRepository.saveAll(List.of(
        buildUser("홍길동", "hong123", "01011111111"),
        buildUser("홍길순", "hong456", "01022222222"),
        buildUser("김철수", "kim001", "01033333333"),
        buildUser("김영희", "kim002", "01011114444")
    ));
    em.flush();
    em.clear(); // 1차 캐시를 비워야 실제 DB를 봄
  }

  private User buildUser(String name, String loginId, String phone) {
    return User.builder()
        .name(name)
        .loginId(loginId)
        .password("encoded_pw")
        .phone(phone)
        .role("USER")
        .build();
  }

  private PageRequest defaultPage() {
    return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
  }

  // ─────────────────────────────────────────────
  // 테스트 케이스
  // ─────────────────────────────────────────────

  @Nested
  @DisplayName("이름 필터링 테스트")
  class NameFilter {

    // ───────────────────────────────────────────
    // 성공 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("이름 '홍' 검색 시 홍씨 2명 반환")
    void searchByName_success() {
      // given
      SearchCondition condition = new SearchCondition("홍", null);

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isEqualTo(2);
      assertThat(result.getContent())
          .extracting(User::getName)
          .containsExactlyInAnyOrder("홍길동", "홍길순");
    }

    @Test
    @DisplayName("이름 null 시 필터 무시 → 전체 4명 반환")
    void searchByName_null_returnsAll() {
      // given
      SearchCondition condition = new SearchCondition(null, null); // 전체 조회

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isEqualTo(4);
    }

    // ───────────────────────────────────────────
    // 실패(엣지) 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("이름 빈 문자열 시 전체 4명 반환 - 빈 문자열은 조건 없음으로 처리")
    void searchByName_empty_returnsAll() {
      // given
      SearchCondition condition = new SearchCondition("", null); // 빈 문자열: hasText() = false

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isEqualTo(4); // 빈 문자열은 필터 미적용
    }

    @Test
    @DisplayName("존재하지 않는 이름 검색 → 빈 페이지 반환")
    void searchByName_notFound_returnsEmpty() {
      // given
      SearchCondition condition = new SearchCondition("없는이름", null);

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isZero();
      assertThat(result.getContent()).isEmpty();
    }
  }

  @Nested
  @DisplayName("전화번호 필터링 테스트")
  class PhoneFilter {

    // ───────────────────────────────────────────
    // 성공 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("전화번호 '01011' 검색 시 홍길동, 김영희 2명 반환")
    void searchByPhone_success() {
      // given
      SearchCondition condition = new SearchCondition(null, "01011");

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isEqualTo(2);
      assertThat(result.getContent())
          .extracting(User::getPhone)
          .allMatch(phone -> phone.contains("01011")); // 결과가 모두 01011을 포함해야 함
    }

    // ───────────────────────────────────────────
    // 실패(엣지) 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("존재하지 않는 전화번호 → 빈 페이지 반환")
    void searchByPhone_notFound_returnsEmpty() {
      // given
      SearchCondition condition = new SearchCondition(null, "99999");

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isZero();
      assertThat(result.getContent()).isEmpty();
    }
  }

  @Nested
  @DisplayName("복합 필터링 테스트")
  class ComboFilter {

    // ───────────────────────────────────────────
    // 성공 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("이름 '홍' + 전화번호 '01011' → 홍길동 1명만 반환")
    void searchByNameAndPhone_success() {
      // given
      SearchCondition condition = new SearchCondition("홍", "01011");
      // 홍씨 2명(홍길동, 홍길순) 중 01011이 포함된 번호는 홍길동뿐

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isEqualTo(1);
      assertThat(result.getContent().get(0).getName()).isEqualTo("홍길동");
    }

    // ───────────────────────────────────────────
    // 실패(엣지) 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("이름, 전화번호 모두 매칭 없음 → 빈 페이지 반환")
    void searchByNameAndPhone_noMatch_returnsEmpty() {
      // given
      SearchCondition condition = new SearchCondition("없는이름", "99999");

      // when
      Page<User> result = userRepository.searchUsers(condition, defaultPage());

      // then
      assertThat(result.getTotalElements()).isZero();
      assertThat(result.getContent()).isEmpty();
    }
  }

  @Nested
  @DisplayName("페이지네이션 테스트")
  class Pagination {

    // ───────────────────────────────────────────
    // 성공 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("size=2, page=0 → 2명만 반환, totalElements=4, isFirst=true")
    void pagination_firstPage() {
      // given
      PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
      SearchCondition condition = new SearchCondition(null, null);

      // when
      Page<User> result = userRepository.searchUsers(condition, pageRequest);

      // then
      assertThat(result.getContent()).hasSize(2);           // 한 페이지에 2명
      assertThat(result.getTotalElements()).isEqualTo(4);   // 전체는 4명
      assertThat(result.getTotalPages()).isEqualTo(2);      // 총 2페이지
      assertThat(result.isFirst()).isTrue();                 // 첫 번째 페이지임
    }

    @Test
    @DisplayName("size=2, page=1 → 나머지 2명 반환, isLast=true")
    void pagination_lastPage() {
      // given
      PageRequest pageRequest = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "id"));
      SearchCondition condition = new SearchCondition(null, null);

      // when
      Page<User> result = userRepository.searchUsers(condition, pageRequest);

      // then
      assertThat(result.getContent()).hasSize(2); // 두 번째 페이지도 2명
      assertThat(result.isLast()).isTrue();        // 마지막 페이지
    }

    // ───────────────────────────────────────────
    // 실패(엣지) 케이스
    // ───────────────────────────────────────────

    @Test
    @DisplayName("범위 초과 페이지 요청 → 빈 페이지 반환 (총 2페이지인데 page=5 요청)")
    void pagination_outOfRange_returnsEmpty() {
      // given
      PageRequest pageRequest = PageRequest.of(5, 2, Sort.by(Sort.Direction.DESC, "id"));
      SearchCondition condition = new SearchCondition(null, null);

      // when
      Page<User> result = userRepository.searchUsers(condition, pageRequest);

      // then
      assertThat(result.getContent()).isEmpty();           // 데이터 없음
      assertThat(result.getTotalElements()).isEqualTo(4);  // 전체 카운트는 여전히 4
    }
  }
}