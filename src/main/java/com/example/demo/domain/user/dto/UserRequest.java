package com.example.demo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequest {

  public static record Create(
      @NotBlank(message = "사용자 이름은 필수입니다.")
      @Size(max = 10, message = "사용자 이름은 최대 10자입니다.")
      String name,

      @NotBlank(message = "로그인 ID는 필수입니다.")
      @Size(max = 20, message = "로그인 ID는 최대 20자입니다.")
      String loginId,

      @NotBlank(message = "비밀번호는 필수입니다.")
      @Size(min = 8, max = 20, message = "비밀번호는 최소 8자 이상, 최대 20자 이하입니다.")
      String password,

      @NotBlank(message = "전화번호는 필수입니다.")
      @Pattern(regexp = "^01[0-9]{8,9}$", message = "하이픈 없이 숫자만 입력해주세요")
      String phone,

      String role
  ) {

  }
}