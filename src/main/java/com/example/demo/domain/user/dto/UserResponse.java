package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.entity.User;

public class UserResponse {

  public record Create(
      Long id,
      String name,
      String loginId,
      String phone,
      String role
  ) {

    // 엔티티 → 응답 DTO 변환
    public static Create from(User user) {
      return new Create(
          user.getId(),
          user.getName(),
          user.getLoginId(),
          user.getFormattedPhone(),
          user.getRole()
      );
    }
  }
}
