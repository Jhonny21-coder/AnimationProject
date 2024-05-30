package com.example.application.data;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Comment {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @ManyToOne
     @JoinColumn(name = "student_info_id")
     private StudentInfo studentInfo;

     private String fullName;
     private Instant dateTime;
     private String comments;

     private String userImage;

     public Long getId() {
        return id;
     }

     public void setId(Long id) {
        this.id = id;
     }

     public StudentInfo getStudentInfo() {
        return studentInfo;
     }

     public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
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
}
