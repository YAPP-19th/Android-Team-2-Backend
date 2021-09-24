package com.yapp.sharefood.user.repository;

import com.yapp.sharefood.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
