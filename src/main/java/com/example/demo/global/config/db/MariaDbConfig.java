package com.example.demo.global.config.db;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "com.example.demo.domain.postMaria.repository",
        "com.example.demo.domain.User.repository"
    }, // Repository 경로
    entityManagerFactoryRef = "mariadbEntityManagerFactory",
    transactionManagerRef = "mariadbTransactionManager"
)
public class MariaDbConfig {

  @Primary
  @Bean(name = "mariadbDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.mariadb")
  public DataSource mariadbDataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Primary
  @Bean(name = "mariadbEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean mariadbEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("mariadbDataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages(
            "com.example.demo.domain.postMaria.entity",
            "com.example.demo.domain.User.entity"
        )        .persistenceUnit("mariadb")
        .build();
  }

  @Primary
  @Bean(name = "mariadbTransactionManager")
  public PlatformTransactionManager mariadbTransactionManager(
      @Qualifier("mariadbEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}