package com.example.application.services;

import com.example.application.data.Artwork;
import com.example.application.data.User;
import com.example.application.repository.UserRepository;
import com.example.application.repository.ArtworkRepository;
import com.example.application.data.StudentInfo;
import com.example.application.repository.StudentInfoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Collections;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;
    private final StudentInfoRepository studentInfoRepository;

    public ArtworkService(ArtworkRepository artworkRepository, StudentInfoRepository studentInfoRepository,
			UserRepository userRepository){
        this.artworkRepository = artworkRepository;
	this.studentInfoRepository = studentInfoRepository;
	this.userRepository = userRepository;
    }

    public void saveArtwork(String email, String artworkUrl, LocalDate dateOfPost,
					LocalTime timeOfPost, String description){
        User user = userRepository.findByEmail(email);
	StudentInfo studentInfo = user.getStudentInfo();

        if (studentInfo != null) {
            Artwork artwork = new Artwork();
            artwork.setStudentInfo(studentInfo);
            artwork.setArtworkUrl(artworkUrl);
            artwork.setDateOfPost(dateOfPost);
	    artwork.setTimeOfPost(timeOfPost);
            artwork.setDescription(description);

            artworkRepository.save(artwork);
        }
    }

    public void updateArtwork(Artwork artwork){
    	artworkRepository.save(artwork);
    }

    public int getArtworksCountByUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return artworkRepository.countByStudentInfoUser(user);
        }
        return 0;
    }

    public List<Artwork> getAllArtworks(){
	return artworkRepository.findAll();
    }

    public List<Artwork> getArtworksByUserId(Long userId) {
        return artworkRepository.findByStudentInfoUserId(userId);
    }

    public Artwork getArtworkById(Long artworkId){
	return artworkRepository.findById(artworkId).orElse(null);
    }

    public void deleteArtwork(Artwork artwork){
    	artworkRepository.delete(artwork);
    }
}
