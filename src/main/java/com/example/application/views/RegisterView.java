 package com.example.application.views;

import com.example.application.services.UserServices;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.datepicker.DatePicker;
import java.time.LocalDate;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.upload.receivers.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

@Route("register")
@PageTitle("Register | TAG")
public class RegisterView extends VerticalLayout {

    private final UserServices userService;
    private final H3 registerHeading = new H3("Register");
    private final TextField firstNameField = new TextField("First Name");
    private final TextField lastNameField = new TextField("Last Name");
    private final TextField ageField = new TextField("Age");
    private final ComboBox<String> genderSelect = new ComboBox<>("Gender");
    private final EmailField emailField = new EmailField("Email");
    private final PasswordField passwordField = new PasswordField("Password");
    private final DatePicker dateOfBirthPicker = new DatePicker("Date of Birth");
    private final TextField placeOfBirthField = new TextField("Place of Birth");
    private final Text profileText = new Text("Profile Image");
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);
    private final Image uploadedImage = new Image();
    private final String OTHER_INFORMATION = "Next";
    private final Button registerButton = new Button("Register");

    public RegisterView(UserServices userService) {
        this.userService = userService;

	addClassName("register-form");

	setWidthFull();
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

	classNames();

	genderSelect.setItems("Male","Female","Bayot");

	int maxFileSizeInBytes = 10 * 1024 * 1024;

	Span fileName = new Span();

	upload.setMaxFileSize(maxFileSizeInBytes);
        upload.setAcceptedFileTypes("image/png", "image/jpeg");
	upload.addSucceededListener(ev -> {
	   String originalFilename = ev.getFileName();
	   fileName.setText(originalFilename);
	});

	//Register button functionality
	registerButton.addClickListener( event -> {
	    try {
		String originalFilename = fileName.getText();
                InputStream inputStream = buffer.getInputStream();
		byte[] bytes = inputStream.readAllBytes();

                // Save the uploaded file to the specified directory
		String filePath = "/data/data/com.termux/files/home/MyVaadinProject/src/main/resources/META-INF/resources/register_images/" + originalFilename;
		FileOutputStream outputStream = new FileOutputStream(filePath);
                outputStream.write(bytes);

                String firstName = firstNameField.getValue();
        	String lastName = lastNameField.getValue();
        	int age = Integer.parseInt(ageField.getValue());
        	String gender = genderSelect.getValue();
        	LocalDate dateOfBirth = dateOfBirthPicker.getValue();
        	String placeOfBirth = placeOfBirthField.getValue();
        	String email = emailField.getValue();
        	String password = passwordField.getValue();

		checkExistence(email, password);

        	// Register user
        	userService.registerUser(firstName, lastName, age, gender, dateOfBirth, placeOfBirth,
                     email, password, originalFilename);

        	// Show notification
        	Notification.show("Registration successful! Please login.", 3000, Position.TOP_CENTER)
              	     .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		getUI().ifPresent(ui -> ui.navigate("login"));
	    } catch (IOException e) {
                    Notification.show("Error uploading artwork image", 3000, Notification.Position.TOP_CENTER);
            }
	});
	registerButton.addClassName("button1");

	handleFileUpload();

	H1 text = new H1("Register");
	text.addClassName("login-text");

	Accordion accordion = new Accordion();
        accordion.addClassName("accordion");

        accordionLayout(accordion);

        // Create login link
        Anchor loginLink = new Anchor("", "Already have an account? Login here.");
	loginLink.addClassName("form-link");

        add(text, accordion, loginLink);

	getThemeList().set(Lumo.DARK, true);
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
	    new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("20em", 2));
        return formLayout;
    }

    public void classNames(){
        firstNameField.addClassName("register-field");
        lastNameField.addClassName("register-field");
        ageField.addClassName("register-field");
        emailField.addClassName("email");
        passwordField.addClassName("password");
        genderSelect.addClassName("gender");
        dateOfBirthPicker.addClassName("date-picker");
        placeOfBirthField.addClassName("register-field");
        upload.addClassName("register-upload");
    }

    public void checkExistence(String email, String password){
        System.out.println("\nIn Register");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        String filename = "src/main/resources/META-INF/resources/data/login.txt";

        boolean isExisting = isDataExisting(filename, email);

        if(isExisting) {
           System.out.println("Data already exists, cannot save.");
        }else{
           try(PrintWriter writer = new PrintWriter(new FileWriter(filename, true))){
                writer.println("Email: " + email);
                writer.println("Password: " + password);
                System.out.println("Successfully saved to " + filename);
           }catch(Exception e) {
                System.out.println(e.getMessage());
           }
        }
    }

    public void handleFileUpload(){
    	// Create a HorizontalLayout to contain the icon and text
        HorizontalLayout uploadLayout = new HorizontalLayout();
        uploadLayout.setSpacing(false);
        uploadLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Create the icon
        Icon uploadIcon = VaadinIcon.FILE_ADD.create();

	Span uploadLabel = new Span("Upload Profile Image (");

        // Create the text
        Span hint = new Span("Accepted images formats: (.png, .jpeg)");
        hint.addClassName("hint");

        Span hintContinue = new Span(")");
        hintContinue.addClassName("hin2");

        // Add both the icon and text to the HorizontalLayout
        uploadLayout.add(uploadIcon, uploadLabel, hint, hintContinue);

        // Create the button and set the uploadLayout as its component
        Button uploadButton = new Button();
        uploadButton.getElement().appendChild(uploadLayout.getElement());

        // Set the uploadButton as the upload button
        upload.setUploadButton(uploadButton);

        upload.addFileRejectedListener(event -> {
            String jsCode = "var audio = new Audio('./images/error.wav'); audio.play();";
            UI.getCurrent().getPage().executeJs(jsCode);

            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        UploadsI18N i18n = new UploadsI18N();

        i18n.getError().setFileIsTooBig(
                "The image exceeds the maximum allowed size of 10MB.");
        i18n.getError().setIncorrectFileType(
                "The provided image does not have the correct format (PNG or JPEG).");
        upload.setI18n(i18n);
    }

    public void accordionLayout(Accordion accordion){
        FormLayout customerDetailsFormLayout = createFormLayout();

        FormLayout formLayout = new FormLayout();
	formLayout.setResponsiveSteps(
            // Use one column by default
            new ResponsiveStep("0", 1),
            // Use two columns, if layout's width exceeds 500px
            new ResponsiveStep("500px", 2)
        );

        // Stretch the username field over 2 columns
        formLayout.setColspan(emailField, 2);

        AccordionPanel mainPanel = accordion.add("Personal Information", formLayout);
	mainPanel.addClassName("panel");

        AccordionPanel customDetailsPanel = accordion.add(OTHER_INFORMATION, customerDetailsFormLayout);
	customDetailsPanel.addClassName("panel");

        // Continue Button
        Icon continueButton = new Icon(VaadinIcon.ARROW_CIRCLE_RIGHT_O);
        continueButton.addClassName("button2");
        continueButton.addClickListener(event -> {
            customDetailsPanel.setOpened(true);
        });

        Icon backIcon = new Icon(VaadinIcon.ARROW_CIRCLE_LEFT_O);
        backIcon.addClassName("back-continue");
        backIcon.addClickListener(event -> {
            mainPanel.setOpened(true);
        });

        // Adding to Form Layout
        formLayout.add(firstNameField, 2);
        formLayout.add(lastNameField, 2);
        formLayout.add(emailField, 2);
        formLayout.add(passwordField, 2);
        mainPanel.addContent(continueButton);

        customerDetailsFormLayout.add(ageField, genderSelect);
        customerDetailsFormLayout.add(dateOfBirthPicker, 2);
        customerDetailsFormLayout.add(placeOfBirthField, 2);
        customerDetailsFormLayout.add(upload, 2);
        customDetailsPanel.addContent(backIcon);
        customDetailsPanel.addContent(registerButton);

        // Setting icons to text field
        firstNameField.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));
        lastNameField.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));
        placeOfBirthField.setSuffixComponent(new Icon(VaadinIcon.WORKPLACE));
        emailField.setSuffixComponent(new Icon(VaadinIcon.ENVELOPE_OPEN_O));
        ageField.setSuffixComponent(new Icon(VaadinIcon.USER_CLOCK));
    }

    public static boolean isDataExisting(String filename, String data) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into label and data parts
                String[] parts = line.split(": ");
                if (parts.length == 2 && parts[1].equals(data)) {
                    return true; // Data exists in the file
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Data does not exist in the file
    }
}
