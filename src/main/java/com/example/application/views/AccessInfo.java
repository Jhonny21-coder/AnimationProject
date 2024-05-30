package com.example.application.views;

import com.example.application.views.form.AddStudentInfo;
import com.example.application.views.form.UpdateUserInfo;
import com.example.application.views.form.UpdateStudentInfo;
import com.example.application.data.StudentInfo;
import com.example.application.services.StudentInfoService;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.data.Artwork;
import com.example.application.services.ArtworkService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.*;
import java.util.logging.Logger;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.router.RouterLink;

@Route("accessInfo")
public class AccessInfo extends AppLayout {

    private final UserServices userService;

    public AccessInfo(UserServices userService){
    	this.userService = userService;
	addClassName("acessinfo-main");

	User user = userService.findCurrentUser();
	StudentInfo student = user.getStudentInfo();

	H3 welcome = new H3("Hello, " + user.getFullName());
	welcome.addClassName("welcome");

	HorizontalLayout footer = createFooter();

	Span firstName = new Span("First name: " + user.getFirstName());
        Span lastName = new Span("Last name: " + user.getLastName());
        Span age = new Span("Age: " + user.getAge());
	Span gender = new Span("Gender: " + user.getGender());
	Span dateOfBirth = new Span("Date of birth: " + user.getDateOfBirth());
	Span placeOfBirth = new Span("Place of birth: " + user.getPlaceOfBirth());
	Span email = new Span("Email: " + user.getEmail());

	firstName.addClassName("acess-details");
 	lastName.addClassName("acess-details");
	age.addClassName("acess-details");
	gender.addClassName("acess-details");
	dateOfBirth.addClassName("acess-details");
	placeOfBirth.addClassName("acess-details");
	email.addClassName("acess-details");

	Div divider = new Div();
        divider.setHeight("1px");
        divider.addClassName("divider");

        VerticalLayout userContent = new VerticalLayout(
	   firstName, lastName, age, gender,
	    dateOfBirth, placeOfBirth, email, divider
	);
        userContent.setSpacing(false);
        userContent.setPadding(false);

        Details userDetails = new Details("Personal information", userContent);
        userDetails.setOpened(false);
	userDetails.addClassName("details");

	VerticalLayout studentContent = new VerticalLayout();

	Div divider2 = new Div();
        divider2.setHeight("1px");
        divider2.addClassName("divider2");

	if(student != null) {
	   Span studentName = new Span("Student number: " + String.valueOf(student.getStudentNumber()));
	   Span penName = new Span("Pen name: " + student.getPenName());
	   Span year = new Span("Year: " + student.getYear());
	   Span position = new Span("Position: " + student.getPosition());
	   Span hobby = new Span("Hobby: " + student.getHobby());

	   studentName.addClassName("acess-details2");
           penName.addClassName("acess-details2");
           year.addClassName("acess-details2");
           position.addClassName("acess-details2");
           hobby.addClassName("acess-details2");
	   studentContent.add(studentName, penName, year, position, hobby, divider2);
	}else{
	   Span noInfoYet = new Span("No student info yet");
	   noInfoYet.addClassName("acess-details2");
	   studentContent.add(noInfoYet, divider2);
	}

	Details studentDetails = new Details("Student Information", studentContent);
	studentDetails.setOpened(false);
	studentDetails.addClassName("details");

	Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("back-icon");
        backIcon.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(EmcView.class));
        });

	HorizontalLayout header = new HorizontalLayout(backIcon, welcome);
	header.setWidthFull();

	VerticalLayout content = new VerticalLayout(userDetails, studentDetails);

	addToNavbar(header);
	addToNavbar(true, footer);

	setContent(content);
    }

    private HorizontalLayout createFooter(){
	RouterLink editLink = new RouterLink();
	RouterLink addLink = new RouterLink();

	editLink.addClassName("access-link");
	addLink.addClassName("access-link");

        editLink.setRoute(UpdateUserInfo.class);
	addLink.setRoute(AddStudentInfo.class);

        Icon editIcon = new Icon(VaadinIcon.TOUCH);
        editIcon.addClassName("edit-icon");

	Icon addIcon = new Icon(VaadinIcon.PLUS_CIRCLE_O);
        addIcon.addClassName("add-icon");

	Icon editStudentIcon = new Icon(VaadinIcon.TOUCH);
	editStudentIcon.addClassName("edit-student-icon");

	Span editStudentSpan = new Span("Edit Student Info");
	editStudentSpan.addClassName("edit-student");

	Span editSpan = new Span("Edit Personal Info");
	editSpan.addClassName("edit");

	Span addSpan = new Span("Add Student Info");
	addSpan.addClassName("add");

	User user = userService.findCurrentUser();

	if(user.getStudentInfo() == null){
           addLink.add(addIcon, addSpan);
	}else {
	   addLink.add(editStudentIcon, editStudentSpan);
	}

	editLink.add(editIcon, editSpan);

	HorizontalLayout footer = new HorizontalLayout(editLink, addLink);
        footer.setWidthFull();

	return footer;
    }
}
