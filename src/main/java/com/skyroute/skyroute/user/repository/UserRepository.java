package com.skyroute.skyroute.user.repository;

import com.skyroute.skyroute.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
