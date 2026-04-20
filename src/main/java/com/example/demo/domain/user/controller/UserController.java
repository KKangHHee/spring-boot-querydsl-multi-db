package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.UserRequest;
import com.example.demo.domain.user.dto.UserResponse;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.response.CommonResponse.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/")
  public ResponseEntity<ApiResponse<UserResponse.Create>> createUser(
      @RequestBody @Valid UserRequest.Create request
  ) {
    User user = userService.createUser(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)          // 201
        .body(ApiResponse.success("유저 생성 성공", UserResponse.Create.from(user)));
  }
}