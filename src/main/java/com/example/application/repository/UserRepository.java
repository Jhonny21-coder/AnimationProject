package com.example.application.repository;

import com.example.application.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByPassword(String password);

}
