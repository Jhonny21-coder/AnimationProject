package com.example.application.data;

import jakarta.persistence.*;

@Entity
public class ReplyReaction {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @ManyToOne
     @JoinColumn(name = "user_id")
     private User reactor;

     @ManyToOne
     @JoinColumn(name = "reply_id")
     private Reply reply;

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

     public Reply getReply(){
        return reply;
     }

     public void setReply(Reply reply){
        this.reply = reply;
     }

     public String getReactType(){
        return reactType;
     }

     public void setReactType(String reactType){
        this.reactType = reactType;
     }
}
