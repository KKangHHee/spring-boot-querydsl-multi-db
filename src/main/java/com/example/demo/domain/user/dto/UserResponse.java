package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.entity.User;
import com.example.demo.global.common.response.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class UserResponse {

  public record Create(
      Long id,

      String name,

      String loginId,

      @JsonView(Views.Manager.class)
      String phone,          // Manager 이상만

      @JsonView(Views.SuperView.class)
      String role            // Super만
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

  public record Summary(
      Long id,
      String name,
      String loginId,
      @JsonView(Views.Manager.class)
      String phone,
      @JsonView(Views.SuperView.class)
      String role
  ) {

    public static Summary from(User user) {
      return new Summary(
          user.getId(),
          user.getName(),
          user.getLoginId(),
          user.getFormattedPhone(),
          user.getRole()
      );
    }
  }
}