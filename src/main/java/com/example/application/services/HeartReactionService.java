package com.example.application.services;

import com.example.application.data.*;
import com.example.application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HeartReactionService {

    private final HeartReactionRepository heartReactionRepository;
    private final UserRepository userRepository;
    private final ArtworkService artworkService;

    @Autowired
    public HeartReactionService(HeartReactionRepository heartReactionRepository,
                UserRepository userRepository, ArtworkService artworkService) {

        this.heartReactionRepository = heartReactionRepository;
        this.userRepository = userRepository;
        this.artworkService = artworkService;

    }

    public List<HeartReaction> getReactionForArtworkId(Long artworkId){
        return heartReactionRepository.findByArtworkId(artworkId);
    }

    // Method to fetch all reactions
    public List<HeartReaction> getAllLikeReactions() {
        return heartReactionRepository.findAll();
    }

    public void saveHeartReaction(String userEmail, Long artworkId, Long heart){

        List<HeartReaction> existingReactions = heartReactionRepository.findByArtworkId(artworkId);

        User user = userRepository.findByEmail(userEmail);

        if (!existingReactions.isEmpty()) {
           boolean userHasReaction = false;
           for (HeartReaction reaction : existingReactions) {
                if (user.getId() == reaction.getUser().getId()) {
                    // User already has a reaction, update it
                    reaction.setHeartReaction(heart);
                    heartReactionRepository.save(reaction);
                    userHasReaction = true;
                    break;
                }
           }
           if (!userHasReaction) {
                // User does not have a reaction, create a new one
                Artwork artwork = artworkService.getArtworkById(artworkId);
                if (artwork != null && user != null) {
                    HeartReaction newReaction = new HeartReaction();
                    newReaction.setUser(user);
                    newReaction.setArtwork(artwork);
                    newReaction.setHeartReaction(heart);
                    heartReactionRepository.save(newReaction);
                }
            }
        } else {
            // If no reactions exist for the artwork, create a new one
            Artwork artwork = artworkService.getArtworkById(artworkId);
            if (artwork != null && user != null) {
                HeartReaction newReaction = new HeartReaction();
                newReaction.setUser(user);
                newReaction.setArtwork(artwork);
                newReaction.setHeartReaction(heart);
                heartReactionRepository.save(newReaction);
            }
        }
    }

    public void removeHeartReaction(String userEmail, Long artworkId) {
        // Find the heart reaction by user email and artwork ID
        HeartReaction heartReaction = heartReactionRepository.findByUserEmailAndArtworkId(userEmail, artworkId);

        // If the heart reaction exists, delete it
        if (heartReaction != null) {
            heartReactionRepository.delete(heartReaction);
        }
    }

    public void deleteHeartReactions(HeartReaction heartReaction){
        heartReactionRepository.delete(heartReaction);
    }
}
