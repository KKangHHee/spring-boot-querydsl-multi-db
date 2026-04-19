package com.example.demo.domain.postOracle.entity;

import com.example.demo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostOracle extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
  private Long id;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, length = 1000)
  private String content;

  @Column(nullable = false, length = 10)
  private String category;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Builder
  private PostOracle(String title, String content, String category, Long userId) {
    this.title = title;
    this.content = content;
    this.category = category;
    this.userId = userId;
  }
}

