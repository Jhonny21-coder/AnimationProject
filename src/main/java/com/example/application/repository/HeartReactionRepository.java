package com.example.application.repository;

import com.example.application.data.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HeartReactionRepository extends JpaRepository<HeartReaction, Long> {

    List<HeartReaction> findByArtworkId(Long artworkId);

    HeartReaction findByUserEmailAndArtworkId(String userEmail, Long artworkId);
}
