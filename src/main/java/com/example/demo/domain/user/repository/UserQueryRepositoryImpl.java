package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.dto.UserRequest;
import com.example.demo.domain.user.entity.QUser;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.config.db.QueryFactoryProvider;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

public class UserQueryRepositoryImpl implements UserQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QUser user = QUser.user;

  public UserQueryRepositoryImpl(QueryFactoryProvider provider) {
    this.queryFactory = provider.maria();
  }

  @Override
  public Page<User> searchUsers(UserRequest.SearchCondition condition, Pageable pageable) {

    List<User> content = queryFactory
        .selectFrom(user)
        .where(
            nameContains(condition.name()),
            phoneContains(condition.phone())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(user.id.desc())
        .fetch();

    Long total = queryFactory
        .select(user.count())
        .from(user)
        .where(
            nameContains(condition.name()),
            phoneContains(condition.phone())
        )
        .fetchOne();

    return new PageImpl<>(content, pageable, total == null ? 0 : total);
  }

  private BooleanExpression nameContains(String name) {
    return StringUtils.hasText(name) ? user.name.contains(name) : null;
  }

  private BooleanExpression phoneContains(String phone) {
    return StringUtils.hasText(phone) ? user.phone.contains(phone) : null;
  }

}
