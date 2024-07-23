package com.berkhayta.repository;

import com.berkhayta.entity.Follow;
import com.berkhayta.entity.User;
import com.berkhayta.utility.FollowState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository  extends JpaRepository<Follow,Long> {
    Optional<Follow> findOptionalByUserIdAndFollowId(Long userId, Long followId);

    List<Follow> findAllByUserId(Long userId);

    List<Follow> findAllByUserIdAndStateIn(Long userId, List<FollowState> takipEdiyor);
}
