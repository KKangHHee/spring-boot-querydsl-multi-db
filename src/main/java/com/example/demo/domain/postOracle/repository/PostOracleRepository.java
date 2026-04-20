package com.example.demo.domain.postOracle.repository;

import com.example.demo.domain.postOracle.entity.PostOracle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostOracleRepository extends JpaRepository<PostOracle, Long> {

}
