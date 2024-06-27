package com.example.application.repository;

import com.example.application.data.Comment;
import com.example.application.data.CommentReaction;
import com.example.application.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    List<CommentReaction> findByCommentId(Long commentId);
    CommentReaction findByReactorIdAndCommentId(Long reactorId, Long commentId);
    CommentReaction findByComment(Comment comment);
}
