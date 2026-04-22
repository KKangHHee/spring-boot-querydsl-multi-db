package com.example.demo.global.common.response;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface ViewResolutionStrategy {
  Class<?> resolve(Collection<? extends GrantedAuthority> authorities, Class<?> defaultView);
}
