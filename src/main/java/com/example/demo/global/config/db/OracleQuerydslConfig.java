package com.example.demo.global.config.db;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OracleQuerydslConfig {

  @PersistenceContext(unitName = "oracle")
  private EntityManager em;

  @Bean(name = "oracleQueryFactory")
  public JPAQueryFactory oracleQueryFactory() {
    return new JPAQueryFactory(em);
  }
}
