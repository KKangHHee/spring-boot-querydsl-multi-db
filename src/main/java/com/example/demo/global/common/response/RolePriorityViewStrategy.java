package com.example.demo.global.common.response;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class RolePriorityViewStrategy implements ViewResolutionStrategy {

  private static final Map<String, Class<?>> ROLE_VIEW_MAP = Map.of(
      "ROLE_SUPER", Views.SuperView.class,
      "ROLE_MANAGER", Views.Manager.class,
      "ROLE_USER", Views.User.class
  );

  private static final List<String> ROLE_PRIORITY = List.of(
      "ROLE_SUPER", "ROLE_MANAGER", "ROLE_USER"
  );

  @Override
  public Class<?> resolve(Collection<? extends GrantedAuthority> authorities,
      Class<?> defaultView) {
    if (authorities == null || authorities.isEmpty()) {
      return defaultView;
    }

    Set<String> userRoles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());

    for (String role : ROLE_PRIORITY) {
      if (userRoles.contains(role)) {
        return ROLE_VIEW_MAP.get(role);
      }
    }

    return defaultView;
  }
}