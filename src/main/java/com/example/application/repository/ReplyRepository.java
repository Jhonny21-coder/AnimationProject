package com.example.application.repository;

import com.example.application.data.Comment;
import com.example.application.data.Reply;
import com.example.application.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByCommentId(Long commentId);

    //Reply findByReplyId(Long replyId);
}
