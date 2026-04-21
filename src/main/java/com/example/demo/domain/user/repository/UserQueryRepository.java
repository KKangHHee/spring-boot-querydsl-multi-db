package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.dto.UserRequest;
import com.example.demo.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

  Page<User> searchUsers(UserRequest.SearchCondition condition, Pageable pageable);
}
