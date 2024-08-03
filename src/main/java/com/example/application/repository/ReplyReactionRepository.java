package com.example.application.repository;

import com.example.application.data.Reply;
import com.example.application.data.ReplyReaction;
import com.example.application.data.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ReplyReactionRepository extends JpaRepository<ReplyReaction, Long> {

    List<ReplyReaction> findByReplyId(Long replyId);
    ReplyReaction findByReactorIdAndReplyId(Long reactorId, Long replyId);
    ReplyReaction findByReply(Reply reply);
}
