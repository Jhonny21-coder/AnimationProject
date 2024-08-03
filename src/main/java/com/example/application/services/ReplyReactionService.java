package com.example.application.services;

import com.example.application.data.ReplyReaction;
import com.example.application.repository.ReplyReactionRepository;
import com.example.application.data.Reply;
import com.example.application.repository.ReplyRepository;
import com.example.application.data.User;
import com.example.application.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.vaadin.flow.server.VaadinSession;
import java.util.Optional;

import java.util.List;
import java.util.Date;
import java.time.Instant;

@Service
public class ReplyReactionService {

    private final ReplyRepository replyRepository;
    private final ReplyReactionRepository replyReactionRepository;
    private final UserRepository userRepository;

    public ReplyReactionService(ReplyRepository replyRepository, UserRepository userRepository,
    	ReplyReactionRepository replyReactionRepository){

        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.replyReactionRepository = replyReactionRepository;
    }

    public List<ReplyReaction> getAllReplyReactions() {
        return replyReactionRepository.findAll();
    }

    public void saveReplyReaction(Reply reply, User reactor, String reactType) {
        if(reactor != null && reply != null){
            ReplyReaction replyReaction = new ReplyReaction();
            replyReaction.setReactor(reactor);
            replyReaction.setReply(reply);
            replyReaction.setReactType(reactType);

            replyReactionRepository.save(replyReaction);
        }
    }

    public List<ReplyReaction> getReplyReactionsByReplyId(Long replyId) {
        return replyReactionRepository.findByReplyId(replyId);
    }

    public ReplyReaction getReplyReactionByReactorAndReplyId(Long reactorId, Long replyId){
    	return replyReactionRepository.findByReactorIdAndReplyId(reactorId, replyId);
    }

    public void removeReplyReaction(Long reactorId, Long replyId) {
        // Find the like reaction by user email and artwork ID
        ReplyReaction reaction = replyReactionRepository.findByReactorIdAndReplyId(reactorId, replyId);

        // If the like reaction exists, delete it
        if (reaction != null) {
            replyReactionRepository.delete(reaction);
        }
    }

    public void updateReplyReaction(ReplyReaction reactor, String reactType){
    	reactor.setReactType(reactType);

    	replyReactionRepository.save(reactor);
    }

    public ReplyReaction getReplyReactionByReply(Reply reply){
    	return replyReactionRepository.findByReply(reply);
    }
}
