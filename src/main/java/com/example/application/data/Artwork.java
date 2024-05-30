package com.example.application.data;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_info_id")
    private StudentInfo studentInfo;

    @NotEmpty(message = "Please provide the image")
    private String artworkUrl;

    @NotNull(message = "Please provide the date")
    private LocalDate dateOfPost;

    @NotNull(message = "Please provide the time")
    private LocalTime timeOfPost;

    @NotEmpty(message = "Please provide the title")
    private String description;

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

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public LocalDate getDateOfPost() {
        return dateOfPost;
    }

    public void setDateOfPost(LocalDate dateOfPost) {
        this.dateOfPost = dateOfPost;
    }

    public LocalTime getTimeOfPost() {
        return timeOfPost;
    }

    public void setTimeOfPost(LocalTime timeOfPost) {
        this.timeOfPost = timeOfPost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
