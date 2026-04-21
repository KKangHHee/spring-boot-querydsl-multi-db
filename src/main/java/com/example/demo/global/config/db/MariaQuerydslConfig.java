package com.example.demo.global.config.db;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MariaQuerydslConfig {

  @PersistenceContext(unitName = "mariadb")
  private EntityManager em;

  @Bean(name = "mariadbQueryFactory")
  @Primary
  public JPAQueryFactory mariadbQueryFactory() {
    return new JPAQueryFactory(em);
  }
}