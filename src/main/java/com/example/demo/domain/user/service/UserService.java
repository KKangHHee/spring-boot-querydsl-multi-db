package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRequest;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.Exception.CustomException;
import com.example.demo.global.Exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User createUser(UserRequest.Create request) {
    duplicationLoginId(request.loginId());
    User user = User.builder()
        .name(request.name())
        .loginId(request.loginId())
        .password(passwordEncoder.encode(request.password()))
        .phone(request.phone())
        .role(request.role())
        .build();

    return userRepository.save(user);
  }

  public Page<User> searchUsers(UserRequest.SearchCondition condition, Pageable pageable) {
    return userRepository.searchUsers(condition, pageable);
  }

  public User findUserByLoginId(String loginId) {
    return userRepository.findByLoginId(loginId).orElse(null);
  }

  private void duplicationLoginId(String loginId) {
    if (userRepository.existsByLoginId(loginId)) {
      throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
    }
  }
}
