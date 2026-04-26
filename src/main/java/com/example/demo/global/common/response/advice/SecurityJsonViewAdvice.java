package com.example.demo.global.common.response.advice;

import com.example.demo.global.common.response.UseJsonView;
import com.example.demo.global.common.response.ViewResolutionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class SecurityJsonViewAdvice extends AbstractMappingJacksonResponseBodyAdvice {

  private final ViewResolutionStrategy viewStrategy;

  @Override
  protected void beforeBodyWriteInternal(
      MappingJacksonValue bodyContainer,
      MediaType contentType,
      MethodParameter returnType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    UseJsonView annotation = returnType.getMethodAnnotation(UseJsonView.class);
    if (annotation == null) {
      return; // @UseJsonView이 있는 곳에만 적용
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // 익명, 미 인증 사용자 처리
    if (isAnonymous(auth)) {
      bodyContainer.setSerializationView(annotation.defaultView());
      return;
    }

    Class<?> view = viewStrategy.resolve(auth.getAuthorities(), annotation.defaultView());
    bodyContainer.setSerializationView(view);
  }

  private boolean isAnonymous(Authentication auth) {
    return auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken;
  }
}