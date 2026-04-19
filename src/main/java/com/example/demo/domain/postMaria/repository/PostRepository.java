package com.example.demo.domain.postMaria.repository;

import com.example.demo.domain.postMaria.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}

