package com.example.application.data;

import jakarta.persistence.*;

@Entity
public class CommentReaction {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @ManyToOne
     @JoinColumn(name = "user_id")
     private User reactor;

     @ManyToOne
     @JoinColumn(name = "comment_id")
     private Comment comment;

     private String reactType;

     public Long getId(){
     	return id;
     }

     public void setId(Long id){
     	this.id = id;
     }

     public User getReactor(){
     	return reactor;
     }

     public void setReactor(User reactor){
     	this.reactor = reactor;
     }

     public Comment getComment(){
     	return comment;
     }

     public void setComment(Comment comment){
     	this.comment = comment;
     }

     public String getReactType(){
     	return reactType;
     }

     public void setReactType(String reactType){
     	this.reactType = reactType;
     }
}
