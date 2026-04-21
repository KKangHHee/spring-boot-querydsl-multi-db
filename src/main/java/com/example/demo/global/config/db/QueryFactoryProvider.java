package com.example.demo.global.config.db;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class QueryFactoryProvider {

  private final JPAQueryFactory mariadbQueryFactory;
  private final JPAQueryFactory oracleQueryFactory;

  public QueryFactoryProvider(
      @Qualifier("mariadbQueryFactory") JPAQueryFactory mariadbQueryFactory,
      @Qualifier("oracleQueryFactory") JPAQueryFactory oracleQueryFactory
  ) {
    this.mariadbQueryFactory = mariadbQueryFactory;
    this.oracleQueryFactory = oracleQueryFactory;
  }

  public JPAQueryFactory maria() {
    return mariadbQueryFactory;
  }

  public JPAQueryFactory oracle() {
    return oracleQueryFactory;
  }
}