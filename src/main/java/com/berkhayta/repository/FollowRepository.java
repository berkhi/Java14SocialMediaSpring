package com.berkhayta.repository;

import com.berkhayta.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository  extends JpaRepository<Follow,Long> {
}
