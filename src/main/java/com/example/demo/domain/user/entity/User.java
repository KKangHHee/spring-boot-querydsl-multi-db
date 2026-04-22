package com.example.demo.domain.user.entity;

import com.example.demo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 10)
  private String name;

  @Column(name = "login_id", nullable = false, length = 20)
  private String loginId;

  @Column(nullable = false, length = 200)
  private String password;

  @Column(nullable = false, length = 11)
  private String phone;

  private String role;

  @Column(name = "failed_count")
  private int failedCount;

  @Column(name = "account_non_locked")
  private boolean accountNonLocked;

  @Builder
  public User(String name, String loginId, String password, String phone, String role) {
    this.name = name;
    this.loginId = loginId;
    this.password = password;
    this.phone = phone;
    this.role = (role == null || role.isBlank()) ? "USER" : role;
    this.accountNonLocked = true;
    this.failedCount = 0;
  }

  public String getFormattedPhone() {
    return this.phone.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
  }

}
