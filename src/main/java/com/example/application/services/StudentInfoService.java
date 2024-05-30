package com.example.application.services;

import com.example.application.data.StudentInfo;
import com.example.application.repository.StudentInfoRepository;
import com.example.application.data.User;
import com.example.application.repository.UserRepository;
import com.example.application.data.Artwork;
import com.example.application.repository.ArtworkRepository;
import org.springframework.stereotype.Service;
import com.vaadin.flow.server.VaadinSession;
import java.util.Optional;

import java.util.List;
import java.util.Date;
import java.time.LocalDate;

@Service
public class StudentInfoService {

    private final StudentInfoRepository studentRepository;

    private final UserRepository userRepository;

    public StudentInfoService(StudentInfoRepository studentRepository, UserRepository userRepository){
        this.studentRepository = studentRepository;
	this.userRepository = userRepository;
    }

    public List<StudentInfo> getAllStudentInfos() {
        return studentRepository.findAll();
    }

    public void saveStudentInfo(String email, Long studentNumber, String penName, 
		String year, String position, String hobby) {

        User user = userRepository.findByEmail(email);

	if(user != null){
            StudentInfo student = new StudentInfo();
            student.setUser(user);
	    student.setStudentNumber(studentNumber);
	    student.setPenName(penName);
	    student.setYear(year);
	    student.setPosition(position);
	    student.setHobby(hobby);

            studentRepository.save(student);
	}
    }

    public void updateStudentInfo(StudentInfo studentInfo){
	studentRepository.save(studentInfo);
    }

    public StudentInfo getStudentInfoById(Long studentInfoId) {
        return studentRepository.findById(studentInfoId).orElse(null);
    }
}
