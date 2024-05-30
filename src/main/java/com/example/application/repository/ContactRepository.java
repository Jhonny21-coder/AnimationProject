package com.example.application.repository;

import com.example.application.data.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Contact findByUserId(Long userId);
}
