package com.example.danguen.domain.post.repository;

import com.example.danguen.domain.post.dto.response.ResponsePostSimpleDto;
import org.springframework.data.repository.CrudRepository;

public interface PostRedisRepository extends CrudRepository<ResponsePostSimpleDto, Long> {
}
