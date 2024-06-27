package com.example.application.services;

import com.example.application.data.Artwork;
import com.example.application.repository.ArtworkRepository;
import com.example.application.data.Comment;
import com.example.application.data.StudentInfo;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;
    private final CommentReactionService commentReactionService;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
    	ArtworkRepository artworkRepository, CommentReactionService commentReactionService){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.artworkRepository = artworkRepository;
        this.commentReactionService = commentReactionService;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public void deleteComment(Long reactorId, Long commentId){
    	commentReactionService.removeCommentReaction(reactorId, commentId);

	Comment comment = commentRepository.findById(commentId).orElse(null);

	if(comment != null){
	   commentRepository.delete(comment);
	}
    }

    public Comment getCommentById(Long commentId){
    	return commentRepository.findById(commentId).orElse(null);
    }

    public void saveComment(String email, String fullName, Instant dateTime, String comments, String userImage, Long artworkId) {
        User user = userRepository.findByEmail(email);

	Artwork artwork = artworkRepository.findById(artworkId).orElse(null);

        if(user != null){
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setArtwork(artwork);
            comment.setFullName(fullName);
            comment.setDateTime(dateTime);
            comment.setComments(comments);
	    comment.setUserImage(userImage);

            commentRepository.save(comment);
        }
    }

    public List<Comment> getCommentsByArtworkId(Long artworkId){
    	return commentRepository.findByArtworkId(artworkId);
    }

    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public int getCommentsCountByUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return commentRepository.countByUser(user);
        }
        return 0;
    }

    public int getCommentsCountByArtworkId(Long artworkId){
   	return commentRepository.countByArtworkId(artworkId);
    }

    public Comment getCommentByUserId(Long userId){
    	return commentRepository.findCommentByUserId(userId);
    }
}
