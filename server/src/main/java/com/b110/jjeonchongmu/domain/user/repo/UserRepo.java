package com.b110.jjeonchongmu.domain.user.repo;

import com.b110.jjeonchongmu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 엔티티에 대한 데이터베이스 접근을 담당하는 리포지토리
 */
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    boolean existsByUserEmail(String userEmail);
    Optional<User> findByUserId(Long userId);

    User getUserByUserId(Long userId);

}
