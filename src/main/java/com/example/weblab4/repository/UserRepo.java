package com.example.weblab4.repository;

import com.example.weblab4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String name);
}
