package com.example.application.repository;

import com.example.application.data.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, Long> {

    // Custom query to count the number of followers for a specific user
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.followedUser.id = :userId AND f.isFollowed = true")
    Long countFollowersByUserId(@Param("userId") Long userId);

    /*@Query("SELECT c FROM Follower c " +
           "WHERE c.followedUser.id = :followedUserId " +
           "AND (LOWER(c.follower.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.follower.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Follower> search(@Param("followedUserId") Long followedUserId,
                          @Param("searchTerm") String searchTerm);*/

    @Query("SELECT c FROM Follower c " +
       "WHERE c.followedUser.id = :followedUserId " +
       "AND (LOWER(CONCAT(c.follower.firstName, ' ', c.follower.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Follower> search(@Param("followedUserId") Long followedUserId,
                      	  @Param("searchTerm") String searchTerm);

    @Transactional
    void deleteByFollowedUserIdAndFollowerId(Long followedUserId, Long followerId);

    Follower findByFollowedUserIdAndFollowerId(Long followedUserId, Long followerId);

    List<Follower> findByFollowedUserId(Long followedUserId);

    Follower findByFollowerId(Long followerId);
}
