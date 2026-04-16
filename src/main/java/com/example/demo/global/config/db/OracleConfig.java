package com.example.demo.global.config.db;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.demo.domain.postOracle.repository", // Repository 경로
    entityManagerFactoryRef = "oracleEntityManagerFactory",
    transactionManagerRef = "oracleTransactionManager"
)
public class OracleConfig {

  @Bean(name = "oracleDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.oracle")
  public DataSource oracleDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Bean(name = "oracleEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("oracleDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("com.example.demo.domain.postOracle.entity") // Entity 경로
        .persistenceUnit("oracle")
        .build();
  }

  @Bean(name = "oracleTransactionManager")
  public PlatformTransactionManager oracleTransactionManager(
      @Qualifier("oracleEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}