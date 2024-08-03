package com.example.application.services;

import com.example.application.data.User;
import com.example.application.data.Follower;
import com.example.application.repository.FollowerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowerService {

    private final FollowerRepository followerRepository;

    public FollowerService(FollowerRepository followerRepository) {
        this.followerRepository = followerRepository;
    }

    public void saveFollower(Follower follower) {
        followerRepository.save(follower);
    }

    public void deleteFollowerByFollowedUserId(Long userId, Long followerId) {
        followerRepository.deleteByFollowedUserIdAndFollowerId(userId, followerId);
    }

    public Long countFollowers(Long userId) {
        return followerRepository.countFollowersByUserId(userId);
    }

    public List<Follower> getAllFollowers() {
        return followerRepository.findAll();
    }

    public Follower getFollowerByFollowerId(Long followerId) {
        return followerRepository.findByFollowerId(followerId);
    }

    public Follower getFollowerByFollowedUserIdAndFollowerId(Long followedUserId, Long followerId) {
        return followerRepository.findByFollowedUserIdAndFollowerId(followedUserId, followerId);
    }

    public List<Follower> getFollowersByFollowedUserId(Long followedUserId){
    	return followerRepository.findByFollowedUserId(followedUserId);
    }

    public List<Follower> findAllFollowers(Long followedUserId, String searchTerm) {
    	if (searchTerm.isEmpty() && searchTerm == null) {
    	    return followerRepository.findByFollowedUserId(followedUserId);
    	} else {
	    return followerRepository.search(followedUserId, searchTerm);
	}
    }

    public List<Follower> findAllFollowers(String searchTerm) {
        return followerRepository.searchFollower(searchTerm);
    }
}
