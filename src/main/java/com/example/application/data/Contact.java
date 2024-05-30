package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Contact {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @NotEmpty(message = "Please provide the email")
   private String email;

   @NotEmpty(message = "Please provide the phone number")
   private String phoneNumber;

   @NotEmpty(message = "Please provide the facebook")
   private String facebook;

   @NotEmpty(message = "Please provide the instagram6")
   private String instagram;

   @NotEmpty(message = "Please provide the tiktok")
   private String tiktok;

   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;

   public Long getId(){
	return id;
   }

   public void setId(Long id){
	this.id = id;
   }

   public String getEmail(){
	return email;
   }

   public void setEmail(String email){
	this.email = email;
   }

   public String getPhoneNumber(){
	return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber){
	this.phoneNumber = phoneNumber;
   }

   public String getFacebook(){
	return facebook;
   }

   public void setFacebook(String facebook){
	this.facebook = facebook;
   }

   public String getInstagram(){
	return instagram;
   }

   public void setInstagram(String instagram){
	this.instagram = instagram;
   }

   public String getTiktok(){
	return tiktok;
   }

   public void setTiktok(String tiktok){
	this.tiktok = tiktok;
   }

   public User getUser(){
	return user;
   }

   public void setUser(User user){
	this.user = user;
   }
}
