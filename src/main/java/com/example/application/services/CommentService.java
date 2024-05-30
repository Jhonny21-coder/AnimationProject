package com.example.application.services;

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

    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public void saveComment(String email, String fullName, Instant dateTime, 
		String comments, String userImage) {
        User user = userRepository.findByEmail(email);
	StudentInfo studentInfo = user.getStudentInfo();

        if(studentInfo != null){
            Comment comment = new Comment();
            comment.setStudentInfo(studentInfo);
            comment.setFullName(fullName);
            comment.setDateTime(dateTime);
            comment.setComments(comments);
	    comment.setUserImage(userImage);

            commentRepository.save(comment);
        }
    }

    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByStudentInfoUserId(userId);
    }

    public int getCommentsCountByUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return commentRepository.countByStudentInfoUser(user);
        }
        return 0;
    }
}
