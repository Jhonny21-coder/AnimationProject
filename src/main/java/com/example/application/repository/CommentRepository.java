package com.example.application.repository;

import com.example.application.data.Comment;
import com.example.application.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    int countByUser(User user);
    int countByArtworkId(Long artworkId);

    List<Comment> findByUserId(Long userId);
    List<Comment> findByArtworkId(Long artworkId);

    Comment findCommentByUserId(Long userId);
}
