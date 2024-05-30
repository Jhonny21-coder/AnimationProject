package com.example.application.repository;

import com.example.application.data.StudentInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Long> {

    StudentInfo findUserById(Long userId);

    StudentInfo findByPenName(String penName);
}
