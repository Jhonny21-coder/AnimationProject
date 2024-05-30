package com.example.application.repository;

import com.example.application.data.Artwork;
import com.example.application.data.StudentInfo;
import com.example.application.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

   int countByStudentInfoUser(User user);

   List<Artwork> findByStudentInfoUserId(Long userId);
}
