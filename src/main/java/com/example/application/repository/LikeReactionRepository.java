package com.example.application.repository;

import com.example.application.data.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LikeReactionRepository extends JpaRepository<LikeReaction, Long> {

    List<LikeReaction> findByArtworkId(Long artworkId);

    LikeReaction findByUserEmailAndArtworkId(String userEmail, Long artworkId);
}
