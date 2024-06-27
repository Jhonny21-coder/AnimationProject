package com.example.application.services;

import com.example.application.data.CommentReaction;
import com.example.application.repository.CommentReactionRepository;
import com.example.application.data.Comment;
import com.example.application.repository.CommentRepository;
import com.example.application.data.User;
import com.example.application.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.vaadin.flow.server.VaadinSession;
import java.util.Optional;

import java.util.List;
import java.util.Date;
import java.time.Instant;

@Service
public class CommentReactionService {

    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;
    private final UserRepository userRepository;

    public CommentReactionService(CommentRepository commentRepository, UserRepository userRepository,
    	CommentReactionRepository commentReactionRepository){

        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.commentReactionRepository = commentReactionRepository;
    }

    public List<CommentReaction> getAllCommentReactions() {
        return commentReactionRepository.findAll();
    }

    public void saveCommentReaction(Comment comment, User reactor, String reactType) {
        if(reactor != null && comment != null){
            CommentReaction commentReaction = new CommentReaction();
            commentReaction.setReactor(reactor);
            commentReaction.setComment(comment);
            commentReaction.setReactType(reactType);

            commentReactionRepository.save(commentReaction);
        }
    }

    public List<CommentReaction> getCommentReactionsByCommentId(Long commentId) {
        return commentReactionRepository.findByCommentId(commentId);
    }

    public CommentReaction getCommentReactionByReactorAndCommentId(Long reactorId, Long commentId){
    	return commentReactionRepository.findByReactorIdAndCommentId(reactorId, commentId);
    }

    public void removeCommentReaction(Long reactorId, Long commentId) {
        // Find the like reaction by user email and artwork ID
        CommentReaction reaction = commentReactionRepository.findByReactorIdAndCommentId(reactorId, commentId);

        // If the like reaction exists, delete it
        if (reaction != null) {
            commentReactionRepository.delete(reaction);
        }
    }

    public void updateCommentReaction(CommentReaction reactor, String reactType){
    	reactor.setReactType(reactType);

    	commentReactionRepository.save(reactor);
    }

    public CommentReaction getCommentReactionByComment(Comment comment){
    	return commentReactionRepository.findByComment(comment);
    }
}
