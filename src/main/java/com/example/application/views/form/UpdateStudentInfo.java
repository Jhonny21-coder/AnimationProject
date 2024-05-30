package com.example.application.views.form;

import com.example.application.views.MainLayout;
import com.example.application.data.StudentInfo;
import com.example.application.services.StudentInfoService;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.applayout.*;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

@Route("updateInfo")
public class UpdateStudentInfo extends AppLayout {

    private final TextField studentNumber = new TextField("Student Number");
    private final TextField penName = new TextField("Pen Name");
    private final ComboBox<String> year = new ComboBox<>("Year");
    private final ComboBox<String> position = new ComboBox<>("Position");
    private final ComboBox<String> hobby = new ComboBox<>("Hobby");
    private final Button save = new Button("Save");
    private final Button close = new Button("Close");

    private final StudentInfoService studentInfoService;
    private final UserServices userService;

    public UpdateStudentInfo(StudentInfoService studentInfoService, UserServices userService) {
        this.studentInfoService = studentInfoService;
	this.userService = userService;

	getElement().getStyle().set("display", "flex");
        getElement().getStyle().set("justify-content", "center");
        getElement().getStyle().set("align-items", "center");

	createHeader();

	addClassName("register-form");

	studentNumber.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));
        penName.setSuffixComponent(new Icon(VaadinIcon.PENCIL));

	studentNumber.addClassName("register-field");
        penName.addClassName("register-field");
        year.addClassName("gender");
        position.addClassName("gender");
        hobby.addClassName("gender");

	save.addClassName("save");
        close.addClassName("close");

	year.setItems("1st Year", "2nd Year", "3rd Year", "4th Year");
        position.setItems("President","Vice-President", "Secretary", "No position");
        hobby.setItems("Playing Online Games", "Designing", "Programming",
                "Drawing", "Other");

	User user = userService.findCurrentUser();

        // Add listener to save changes
        save.addClickListener(event -> {
	    ConfirmDialog dialog = new ConfirmDialog();
            dialog.addClassName("view-dialog");
            dialog.open();
            dialog.setCancelable(true);
            dialog.setConfirmText("Okay");
            dialog.setConfirmButtonTheme("primary");
            dialog.setHeader("Are you sure you want to save changes?");
            dialog.setText("This may change your previous user information.");
            dialog.addConfirmListener(events -> {
            	if (user != null) {
		   StudentInfo studentInfo = user.getStudentInfo();

             	   if(studentInfo != null){
            	      studentInfo.setStudentNumber(Long.parseLong(studentNumber.getValue()));
            	      studentInfo.setPenName(penName.getValue());
            	      studentInfo.setYear(year.getValue());
            	      studentInfo.setPosition(position.getValue());
            	      studentInfo.setHobby(hobby.getValue());

            	      studentInfoService.updateStudentInfo(studentInfo);

                      Notification.show("Changes saved successfully", 3000, Notification.Position.TOP_CENTER)
                       	 .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                      getUI().ifPresent(ui -> ui.navigate(AddStudentInfo.class));

                    }else{
                      Notification.show("Please fill in the missing fields", 3000, Notification.Position.TOP_CENTER)
                         .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }

            	} else {
                      Notification.show("Student not found", 3000, Notification.Position.TOP_CENTER)
                         .addThemeVariants(NotificationVariant.LUMO_ERROR);
            	}
	    });
        });

	close.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(AddStudentInfo.class));
        });

        // Populate form fields with current StudentInfo values
        populateFormWithCurrentStudentInfo();

        // Create form layout and add form components
        FormLayout formLayout = new FormLayout();
        formLayout.add(studentNumber, penName, year, position, hobby, new HorizontalLayout(save, close));
	formLayout.addClassName("form-layout");

        // Add form layout to the vertical layout
        setContent(formLayout);
    }

    // Method to populate form fields with current StudentInfo values
    private void populateFormWithCurrentStudentInfo() {
        // Fetch current StudentInfo object
        StudentInfo currentStudentInfo = getCurrentStudentInfo();

        // Set values to form fields
        if (currentStudentInfo != null) {
            studentNumber.setValue(String.valueOf(currentStudentInfo.getStudentNumber()));
            penName.setValue(currentStudentInfo.getPenName());
            year.setValue(currentStudentInfo.getYear());
            position.setValue(currentStudentInfo.getPosition());
            hobby.setValue(currentStudentInfo.getHobby());
        }
    }

    private StudentInfo getCurrentStudentInfo() {
	User user = userService.findCurrentUser();

        return user.getStudentInfo();
    }

    private void createHeader(){
        H1 welcome = new H1("Update Student Information");
        welcome.addClassName("welcome");

        Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("back-icon");
        backIcon.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("addStudentInfo"));
        });

        HorizontalLayout header = new HorizontalLayout(backIcon, welcome);
        header.setWidthFull();

        addToNavbar(header);
    }
}
