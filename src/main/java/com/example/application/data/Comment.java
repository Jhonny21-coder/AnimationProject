package com.example.application.data;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Comment {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     /*@ManyToOne
     @JoinColumn(name = "student_info_id")
     private StudentInfo studentInfo;*/

     @ManyToOne
     @JoinColumn(name = "user_id")
     private User user;

     @ManyToOne
     @JoinColumn(name = "artwork_id")
     private Artwork artwork;

     private String fullName;
     private Instant dateTime;
     private String comments;
     private String userImage;

     private long likeReact;
     private long heartReact;
     private long happyReact;

     public Long getId() {
        return id;
     }

     public void setId(Long id) {
        this.id = id;
     }

     public User getUser() {
        return user;
     }

     public void setUser(User user) {
        this.user = user;
     }

     public Artwork getArtwork(){
     	return artwork;
     }

     public void setArtwork(Artwork artwork){
     	this.artwork = artwork;
     }

     public String getFullName(){
	return fullName;
     }

     public void setFullName(String fullName){
	this.fullName = fullName;
     }

     public Instant getDateTime(){
	return dateTime;
     }

     public void setDateTime(Instant dateTime){
	this.dateTime = dateTime;
     }

     public String getComments(){
	return comments;
     }

     public void setComments(String comments){
	this.comments = comments;
     }

     public String getUserImage() {
        return userImage;
     }

     public void setUserImage(String userImage) {
        this.userImage = userImage;
     }

     public long getLikeReact(){
     	return likeReact;
     }

     public void setLikeReact(long likeReact){
     	this.likeReact = likeReact;
     }

     public long getHeartReact(){
        return heartReact;
     }

     public void setHeartReact(long heartReact){
        this.heartReact = heartReact;
     }

     public long getHappyReact(){
        return happyReact;
     }

     public void setHappyReact(long happyReact){
        this.happyReact = happyReact;
     }
}
