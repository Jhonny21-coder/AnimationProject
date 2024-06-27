package com.example.application.services;

import com.example.application.data.*;
import com.example.application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LikeReactionService {

    private final LikeReactionRepository likeReactionRepository;
    private final UserRepository userRepository;
    private final ArtworkService artworkService;

    @Autowired
    public LikeReactionService(LikeReactionRepository likeReactionRepository,
		UserRepository userRepository,
		ArtworkService artworkService) {

        this.likeReactionRepository = likeReactionRepository;
	this.userRepository = userRepository;
	this.artworkService = artworkService;

    }

    public LikeReaction getReactionByUserAndArtwork(String userEmail, Long artworkId) {
        // Find the like reaction by user email and artwork ID
        return likeReactionRepository.findByUserEmailAndArtworkId(userEmail, artworkId);
    }

    public List<LikeReaction> getReactionForArtworkId(Long artworkId){
	return likeReactionRepository.findByArtworkId(artworkId);
    }

    // Method to fetch all reactions
    public List<LikeReaction> getAllLikeReactions() {
        return likeReactionRepository.findAll();
    }

    public void saveReaction(LikeReaction reaction){
    	likeReactionRepository.save(reaction);
    }

    public void saveLikeReaction(String userEmail, Long artworkId, Long like){

    	List<LikeReaction> existingReactions = likeReactionRepository.findByArtworkId(artworkId);

    	User user = userRepository.findByEmail(userEmail);

    	if (!existingReactions.isEmpty()) {
           boolean userHasReaction = false;
           for (LikeReaction reaction : existingReactions) {
           	if (user.getId() == reaction.getUser().getId()) {
                    // User already has a reaction, update it
                    reaction.setLikeReaction(like);
                    likeReactionRepository.save(reaction);
                    userHasReaction = true;
                    break;
            	}
           }
           if (!userHasReaction) {
           	// User does not have a reaction, create a new one
            	Artwork artwork = artworkService.getArtworkById(artworkId);
            	if (artwork != null && user != null) {
                    LikeReaction newReaction = new LikeReaction();
                    newReaction.setUser(user);
                    newReaction.setArtwork(artwork);
                    newReaction.setLikeReaction(like);
                    likeReactionRepository.save(newReaction);
            	}
            }
    	} else {
            // If no reactions exist for the artwork, create a new one
            Artwork artwork = artworkService.getArtworkById(artworkId);
            if (artwork != null && user != null) {
            	LikeReaction newReaction = new LikeReaction();
            	newReaction.setUser(user);
            	newReaction.setArtwork(artwork);
            	newReaction.setLikeReaction(like);
            	likeReactionRepository.save(newReaction);
            }
    	}
    }

    public void removeLikeReaction(String userEmail, Long artworkId) {
        // Find the like reaction by user email and artwork ID
        LikeReaction likeReaction = likeReactionRepository.findByUserEmailAndArtworkId(userEmail, artworkId);

        // If the like reaction exists, delete it
        if (likeReaction != null) {
            likeReactionRepository.delete(likeReaction);
        }
    }

    public void deleteLikeReactions(LikeReaction likeReaction){
    	likeReactionRepository.delete(likeReaction);
    }
}
