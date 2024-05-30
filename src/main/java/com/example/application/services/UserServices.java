package com.example.application.services;

import com.example.application.data.User;
import com.example.application.repository.UserRepository;
import com.example.application.data.StudentInfo;
import com.example.application.repository.StudentInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserServices {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final StudentInfoRepository studentInfoRepository;

    public UserServices(UserRepository userRepository, StudentInfoRepository studentInfoRepository){
	this.userRepository = userRepository;
	this.studentInfoRepository = studentInfoRepository;
    }

    public void updateUser(User user){
         userRepository.save(user);
    }

    public void registerUser(String firstName, String lastName, int age,
	String gender, LocalDate dateOfBirth, String placeOfBirth,
	String email, String password, String profileImage) {

        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(password);

        // Create a new user entity
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);
        user.setPlaceOfBirth(placeOfBirth);
        user.setEmail(email);
        user.setPassword(encryptedPassword); // Store the encrypted password
	user.setProfileImage(profileImage);
	user.setRole("USER");

        // Save the user entity in the database
       	userRepository.save(user);
    }

    public boolean authenticate(String email, String password) {
        // Retrieve the user from the database
        User user = userRepository.findByEmail(email);

        // Check if the user exists and if the provided password matches the stored encrypted password
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User findCurrentUser() {
        // Retrieve the email of the current user from the session
        String userEmail = (String) VaadinSession.getCurrent().getAttribute("user");

        // Use the email to fetch the user from the database
        if (userEmail != null) {
            return userRepository.findByEmail(userEmail);
        }
        return null;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findUserByEmail(String userEmail){
    	return userRepository.findByEmail(userEmail);
    }

    public User findUserByPassword(String userPassword){
    	User user = findCurrentUser();

    	if(passwordEncoder.matches(userPassword, user.getPassword())){
    	   return userRepository.findByEmail(user.getEmail());
    	}
    	return null;
    }
}
