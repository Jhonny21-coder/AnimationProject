package com.example.application.views.form;

import com.example.application.views.MainLayout;
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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import jakarta.annotation.security.PermitAll;

@Route("addStudentInfo")
public class AddStudentInfo extends AppLayout {

    private final StudentInfoService studentInfoService;
    private final UserServices userService;
    private final TextField studentNumber = new TextField("Student Number");
    private final TextField penName = new TextField("Pen Name");
    private final ComboBox<String> year = new ComboBox<>("Year");
    private final ComboBox<String> position = new ComboBox<>("Position");
    private final ComboBox<String> hobby = new ComboBox<>("Hobby");
    private final Button save = new Button("Save");
    private final Button close = new Button("Close");

    public AddStudentInfo(StudentInfoService studentInfoService, UserServices userService) {
        this.studentInfoService = studentInfoService;
        this.userService = userService;

	createHeader();

	addClassName("register-form");

	studentNumber.addClassName("register-field");
	penName.addClassName("register-field");
	year.addClassName("gender");
	position.addClassName("gender");
	hobby.addClassName("gender");

	User user = userService.findCurrentUser();
        if(user.getStudentInfo() == null){

	studentNumber.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));
	penName.setSuffixComponent(new Icon(VaadinIcon.PENCIL));

	save.addClassName("save");
	close.addClassName("close");

	year.setItems("1st Year", "2nd Year", "3rd Year", "4th Year");
	position.setItems("President","Vice-President", "Secretary", "No position");
	hobby.setItems("Playing Online Games", "Designing", "Programming",
		"Drawing", "Other");

        save.addClickListener(event -> {
            if (user != null) {
		String emailValue = user.getEmail();
                Long studentNumberValue = Long.parseLong(studentNumber.getValue());
		String penNameValue = penName.getValue();
		String yearValue = year.getValue();
		String positionValue = position.getValue();
		String hobbyValue = hobby.getValue();

		if(emailValue != null && studentNumberValue != null && penNameValue != null && yearValue != null && positionValue != null && hobbyValue != null){
		   studentInfoService.saveStudentInfo(
			emailValue,
			studentNumberValue,
			penNameValue,
			yearValue,
			positionValue,
			hobbyValue
                   );
                   Notification.show("Artwork saved successfully", 3000, Notification.Position.TOP_CENTER)
		   	.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		   getUI().ifPresent(ui -> ui.navigate("emc-view"));

		}else{
		   Notification.show("Please fill in the missing fields", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
		}

            } else {
                Notification.show("Student not found", 3000, Notification.Position.TOP_CENTER)
			.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        close.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("emc-view"));
        });

	FormLayout formLayout = new FormLayout();
        formLayout.add(studentNumber, penName, year, position, hobby, new HorizontalLayout(save, close));
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 3));
	addClassName("addstudent-layout");

        setContent(formLayout);

        }else{
	    Button updateButton = new Button("Edit");
	    updateButton.addClassName("update-button");
	    updateButton.addClickListener(event -> {
		getUI().ifPresent(ui -> ui.navigate(UpdateStudentInfo.class));
	    });

	    setContent(updateButton);
        }
    }

    private void createHeader(){
        H1 welcome = new H1("Update Student Information");
        welcome.addClassName("welcome");

        Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("back-icon");
        backIcon.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("accessInfo"));
        });

        HorizontalLayout header = new HorizontalLayout(backIcon, welcome);
        header.setWidthFull();

        addToNavbar(header);
    }
}
