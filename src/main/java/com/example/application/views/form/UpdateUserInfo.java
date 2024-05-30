package com.example.application.views.form;

import com.example.application.repository.UserRepository;
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
import com.vaadin.flow.component.applayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

@Route("updateUser")
public class UpdateUserInfo extends AppLayout {

    private final UserRepository userRepository;
    private final UserServices userService;
    private String originalFilename;
    private final TextField firstNameField = new TextField("First Name");
    private final TextField lastNameField = new TextField("Last Name");
    private final TextField ageField = new TextField("Age");
    private final ComboBox<String> genderSelect = new ComboBox<>("Gender");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private final DatePicker dateOfBirthPicker = new DatePicker("Date of Birth");
    private final TextField placeOfBirthField = new TextField("Place of Birth");
    private final String OTHER_INFORMATION = "Next";
    private final Button save = new Button("Save");
    private final Button close = new Button("Close");

    private FormLayout createFormLayout() {
        FormLayout billingAddressFormLayout = new FormLayout();
        billingAddressFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));
        return billingAddressFormLayout;
    }

    public UpdateUserInfo(UserServices userService, UserRepository userRepository) {
        this.userService = userService;
	this.userRepository = userRepository;

	createHeader();

        addClassName("register-form");
	firstNameField.addClassName("custom-field");
        lastNameField.addClassName("register-field");
        ageField.addClassName("custom-field");
        emailField.addClassName("email");
        passwordField.addClassName("password");
        genderSelect.addClassName("gender");
        dateOfBirthPicker.addClassName("date-picker");
        placeOfBirthField.addClassName("register-field");
	save.addClassName("save");
        close.addClassName("close");

	genderSelect.setItems("Male","Female","Bayot");

	passwordField.setReadOnly(true);

	// Setting icons as suffix
	firstNameField.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));
        lastNameField.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));
        placeOfBirthField.setSuffixComponent(new Icon(VaadinIcon.WORKPLACE));
	emailField.setSuffixComponent(new Icon(VaadinIcon.ENVELOPE_OPEN_O));
        ageField.setSuffixComponent(new Icon(VaadinIcon.USER_CLOCK));

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
                   user.setFirstName(firstNameField.getValue());
                   user.setLastName(lastNameField.getValue());
                   user.setAge(Integer.parseInt(ageField.getValue()));
                   user.setGender(genderSelect.getValue());
                   user.setEmail(emailField.getValue());
		   user.setDateOfBirth(dateOfBirthPicker.getValue());
	      	   user.setPlaceOfBirth(placeOfBirthField.getValue());

            	   userRepository.save(user);

            	   Notification.show("Changes saved successfully", 3000, Notification.Position.TOP_CENTER)
            	        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            	   getUI().ifPresent(ui -> ui.navigate("accessInfo"));
            	} else {
            	   Notification.show("Student not found", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            	}
	    });
        });

        close.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("accessInfo"));
        });

        // Populate form fields with current StudentInfo values
        populateFormWithCurrentUser();

	// Accordion
        Accordion accordion = new Accordion();

        FormLayout customerDetailsFormLayout = createFormLayout();
        FormLayout formLayout = new FormLayout();

        AccordionPanel mainPanel = accordion.add("Personal Information", formLayout);

        AccordionPanel customDetailsPanel = accordion.add(OTHER_INFORMATION,
                customerDetailsFormLayout);

        mainPanel.addClassName("panel");
        customDetailsPanel.addClassName("panel");
        accordion.addClassName("accordion");

        // Continue Button
        Icon continueButton = new Icon(VaadinIcon.ARROW_CIRCLE_RIGHT_O);
        continueButton.addClickListener(event -> {
                customDetailsPanel.setOpened(true);
        });
	continueButton.addClassName("button2");

	Icon backIcon = new Icon(VaadinIcon.ARROW_CIRCLE_LEFT_O);
        backIcon.addClickListener(event -> {
                mainPanel.setOpened(true);
        });
        backIcon.addClassName("back-continue");

	formLayout.add(firstNameField, 2);
        formLayout.add(lastNameField, 2);
        formLayout.add(emailField, 2);
        formLayout.add(passwordField, 2);
        mainPanel.addContent(continueButton);

        customerDetailsFormLayout.add(ageField, genderSelect);
        customerDetailsFormLayout.add(dateOfBirthPicker, 2);
        customerDetailsFormLayout.add(placeOfBirthField, 2);
	customerDetailsFormLayout.add(new HorizontalLayout(save, close));
        customDetailsPanel.addContent(backIcon);

	setContent(accordion);
    }

    // Method to populate form fields with current StudentInfo values
    private void populateFormWithCurrentUser() {
        // Fetch current StudentInfo object (replace this with your logic)
        User currentUser = userService.findCurrentUser();

        // Set values to form fields
        if (currentUser != null) {
            firstNameField.setValue(currentUser.getFirstName());
            lastNameField.setValue(currentUser.getLastName());
            ageField.setValue(String.valueOf(currentUser.getAge()));
            genderSelect.setValue(currentUser.getGender());
            emailField.setValue(currentUser.getEmail());
	    passwordField.setValue("Cannot change your password here");
	    dateOfBirthPicker.setValue(currentUser.getDateOfBirth());
	    placeOfBirthField.setValue(currentUser.getPlaceOfBirth());
        }
    }

    private void createHeader(){
	H1 welcome = new H1("Update User Information");
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
