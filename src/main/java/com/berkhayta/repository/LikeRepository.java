package com.berkhayta.repository;

import com.berkhayta.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository  extends JpaRepository<Like,Long> {
}
