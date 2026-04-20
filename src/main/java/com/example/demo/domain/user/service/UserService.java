package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRequest;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

  public User findUserByLoginId(String loginId) {
    return userRepository.findByLoginId(loginId).orElse(null);
  }

  private void duplicationLoginId(String loginId) {
    if (userRepository.existsByLoginId(loginId)) {
      throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
    }
  }
}
