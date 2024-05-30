package com.example.application.services;

import com.example.application.data.Contact;
import com.example.application.data.User;
import com.example.application.repository.UserRepository;
import com.example.application.repository.ContactRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository){
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    public void saveContact(String email, String newEmail, String phoneNumber,
	String facebook, String instagram, String tiktok){

        User user = userRepository.findByEmail(email);

        if (user != null) {
            Contact contact = new Contact();
	    contact.setUser(user);
	    contact.setEmail(newEmail);
	    contact.setPhoneNumber(phoneNumber);
	    contact.setFacebook(facebook);
	    contact.setInstagram(instagram);
	    contact.setTiktok(tiktok);

	    contactRepository.save(contact);
        }
    }

    public Contact getContactByUserId(Long userId) {
        return contactRepository.findByUserId(userId);
    }
}
