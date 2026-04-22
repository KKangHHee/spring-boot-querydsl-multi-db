package com.example.demo.global.config;

import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    // 필드에 안 걸려있으면 허용 상태로
    return builder -> builder.featuresToEnable(MapperFeature.DEFAULT_VIEW_INCLUSION);
  }
}