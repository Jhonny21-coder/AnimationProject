package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
public class StudentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Please provide the student number")
    private Long studentNumber;

    @NotEmpty(message = "Please provide the pen name")
    private String penName;

    @NotEmpty(message = "Please provide the year")
    private String year;

    @NotEmpty(message = "Please provide the position")
    private String position;

    @NotEmpty(message = "Please provide the hobby")
    private String hobby;

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

    public Long getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Long studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getPenName(){
	return penName;
    }

    public void setPenName(String penName){
	this.penName = penName;
    }

    public String getYear(){
	return year;
    }

    public void setYear(String year){
	this.year = year;
    }

    public String getPosition(){
	return position;
    }

    public void setPosition(String position){
	this.position = position;
    }

    public String getHobby(){
	return hobby;
    }

    public void setHobby(String hobby){
	this.hobby = hobby;
    }
}
