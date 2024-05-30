package com.example.application.views.form;

import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.security.PermitAll;

import org.apache.commons.text.RandomStringGenerator;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route("forgotPassword")
public class ForgotPassword extends VerticalLayout {
    private final UserServices userService;
    private final String EMAIL_USERNAME = "shanemarahay@gmail.com";
    private final String EMAIL_PASSWORD = "azfi yhun igli arcc";
    private String generatedOTP;
    private String userInputOTP = "";
    private HorizontalLayout verificationCodeLayout;

    // Find existing email
    private EmailField emailField = new EmailField("Please enter your email");
    private Button sendVerificationBtn = new Button("Send Verification");
    private H2 text11 = new H2("Forgot Password");
    private H3 text22 = new H3("Lost your password?");
    private Span text33 = new Span("Recover here through email address");

    // Back buttons
    private Button cancelButton1 = new Button("Cancel");
    private Button cancelButton2 = new Button("Cancel");
    private Button cancelButton3 = new Button("Cancel");

    // Form layouts
    private FormLayout formLayout1 = new FormLayout();
    private FormLayout formLayout2 = new FormLayout();
    private FormLayout formLayout3 = new FormLayout();

    // Verify password
    private Button verifyButton = new Button("Verify");
    private H2 text1 = new H2("Verify with Email");
    private Span text2 = new Span("We sent a 6-digit verification code to your email.");
    private H3 text3 = new H3();
    private Span text4 = new Span("Please enter the verification code");
    private Span sendAgain = new Span("Didn't recieve the code?");
    private Span resend = new Span("Resend code.");
    private Span maskedSpan = new Span();

    // Recover password
    private TextField newPasswordField = new TextField();
    private Button resetButton = new Button("Reset");
    private H2 resetText1 = new H2("Reset your password");
    private Span resetText2 = new Span("Please enter your new password");

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ForgotPassword(UserServices userService){
        this.userService = userService;

	setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

	backButtonListener();
        findExistingEmail();

        addClassName("register-form");
     }

     public void findExistingEmail(){
	text11.getStyle().set("font-family", "serif");
        text22.getStyle().set("font-family", "serif");
        text33.getStyle().set("font-family", "serif");

	emailField.setPlaceholder("company@example.com");
        emailField.setErrorMessage("Please enter a valid email address");
        emailField.setSuffixComponent(new Icon(VaadinIcon.ENVELOPE));
        emailField.addClassName("email");

        sendVerificationBtn.addClassName("save");
        sendVerificationBtn.addClickListener(e -> {
            String emailValue = emailField.getValue();
            User user = userService.findUserByEmail(emailValue);

            if(user != null){
                verifyPassword(emailValue);
            }else if(emailField.getValue().isEmpty()){
            	Notification.show("Email cannot be empty", 2000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }else{
                Notification.show("No user found with the given email", 2000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        formLayout1.add(emailField, sendVerificationBtn, cancelButton1);

        add(text11, text22, text33, formLayout1);
    }

    public void verifyPassword(String userEmail) {
    	User user = userService.findUserByEmail(userEmail);

        generatedOTP = generateOTP(6);
        sendVerificationEmail(userEmail);

        String[] parts = userEmail.split("@");

	if (parts.length == 2) {
            // Get the local part and domain part
            String localPart = parts[0];
            String domainPart = parts[1];

            int visibleLength = 3;
	    String maskedEmail = "";

	    // Ensure at least the first 3 characters are visible
            if(localPart.length() <= visibleLength){
            	// If local part is too short, just use it as is
            	maskedEmail = localPart + "@" + domainPart;
            }else{
            	// Create masked part
            	String maskedPart = "*".repeat(localPart.length() - visibleLength);

		// Combine visible part with masked part
            	maskedEmail = localPart.substring(0, visibleLength) + maskedPart + "@" + domainPart;
            }

            // Set masked email
            text3.setText(maskedEmail);
            text3.addClassName("text4");
        } else {
            System.out.println("Invalid email address");
        }

	text4.addClassName("text4");

	text1.getStyle().set("font-family", "serif");
	text2.getStyle().set("font-family", "serif");
	text3.getStyle().set("font-family", "serif");
	text4.getStyle().set("font-family", "serif");
	sendAgain.getStyle().set("font-family", "serif");
	resend.getStyle().set("color", "#0ef");

	resend.addClickListener(event -> {
	    sendVerificationEmail(userEmail);

	    resetVerificationCodeFields(verificationCodeLayout);
	});

	verifyButton.addClassName("save");
        verifyButton.addClickListener(event -> {
            boolean isEmpty = false;
            for (Component component : verificationCodeLayout.getChildren().toArray(Component[]::new)) {
            	if (((TextField) component).getValue().isEmpty()){
            	   isEmpty = true;
            	}
            }
            if (isEmpty){
                Notification.show("Please complete the verification code", 2000, Notification.Position.MIDDLE)
		    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }else{
                if (verifyOTP(userInputOTP, generatedOTP)){
                    Notification.show("You have been verified successfully", 2000, Notification.Position.TOP_CENTER)
                 	.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

		    recoverPassword(user);
		}else{
                    Notification.show("Invalid verification code", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });

	verificationCodeLayout = generateVerificationCodeFields(6);

	formLayout2.add(verifyButton, cancelButton2);

	remove(formLayout1, text11, text22, text33);

        add(text1, text2, text3, text4, verificationCodeLayout, sendAgain, resend, formLayout2);
    }

    public void recoverPassword(User user){
	newPasswordField.addClassName("register-field");
	newPasswordField.setPlaceholder("Enter new password");
	resetButton.addClassName("save");

	ConfirmDialog dialog = new ConfirmDialog();
	dialog.addClassName("view-dialog");
	dialog.setCancelable(true);
	dialog.setConfirmText("Okay");
	dialog.setConfirmButtonTheme("primary");
	dialog.setHeader("Are you sure you want to reset your password?");
	dialog.addConfirmListener(event -> {
	     String newPasswordValue = newPasswordField.getValue();
             String encryptedPassword = passwordEncoder.encode(newPasswordValue);

             user.setPassword(encryptedPassword);
             userService.updateUser(user);

             Notification.show("Password changed successfully", 3000, Notification.Position.TOP_CENTER)
                 .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

             getUI().ifPresent(ui -> ui.navigate(""));
	});

	resetButton.addClickListener(event -> {
	     String newPasswordValue = newPasswordField.getValue();

	     if(newPasswordValue.isEmpty()){
	     	Notification.show("Password cannot be empty", 1000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
	     }else{
	     	dialog.open();
	     }
	});

	formLayout3.add(newPasswordField, resetButton, cancelButton3);

	remove(text1, text2, text3, text4, verificationCodeLayout, sendAgain, resend, formLayout2);

	add(resetText1, resetText2, formLayout3);
    }

    private void backButtonListener(){
        cancelButton1.addClassName("save");
        cancelButton2.addClassName("save");
        cancelButton3.addClassName("save");

        cancelButton1.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        cancelButton2.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        cancelButton3.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });
    }

    private void resetVerificationCodeFields(HorizontalLayout layout) {
        for (Component component : layout.getChildren().toArray(Component[]::new)) {
            if (component instanceof TextField) {
                ((TextField) component).clear();
            }
        }
    }

    private HorizontalLayout generateVerificationCodeFields(int length) {
        HorizontalLayout layout = new HorizontalLayout();

        for (int i = 0; i < length; i++) {
            TextField digitField = new TextField();
            digitField.addClassName("digit-field");
            digitField.setMaxLength(1);
            digitField.addValueChangeListener(event -> {
                String value = event.getValue();
                userInputOTP = getUserInputOTP();

                if (!value.isEmpty()) {
                    int currentIndex = layout.getChildren().collect(Collectors.toList()).indexOf(digitField);
                    if (currentIndex < layout.getChildren().count() - 1) {
                        TextField nextField = (TextField) layout.getChildren().toArray()[currentIndex + 1];
                        nextField.focus();
                    }
                }
            });
            layout.add(digitField);
        }
        return layout;
    }

    private String getUserInputOTP() {
        StringBuilder numbers = new StringBuilder();

        for (Component component : verificationCodeLayout.getChildren().toArray(Component[]::new)) {
            TextField digitField = (TextField) component;
            numbers.append(digitField.getValue());
        }
        return numbers.toString();
    }

    public String generateOTP(int length) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public boolean verifyOTP(String userInputOTP, String generatedOTP) {
        return userInputOTP.equals(generatedOTP);
    }

    public void sendVerificationEmail(String userEmail) {
    	Notification.show("Verification sent successfully", 3000, Notification.Position.TOP_CENTER)
		.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	// Generate OTP
        generatedOTP = generateOTP(6);

	// Email configuration
	String host = "smtp.gmail.com";
	Properties properties = System.getProperties();
	properties.setProperty("mail.smtp.host", host);
	properties.setProperty("mail.smtp.port", "587");
	properties.setProperty("mail.smtp.auth", "true");
	properties.setProperty("mail.smtp.starttls.enable", "true");

	// Authenticator to log in to your email account
	Authenticator authenticator = new Authenticator() {
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
		}
	};

	// Create a session with the email server
	Session session = Session.getDefaultInstance(properties, authenticator);

	try {
	   // Create a default MimeMessage object
	   MimeMessage message = new MimeMessage(session);

	   // Set From: header field of the header
	   message.setFrom(new InternetAddress(EMAIL_USERNAME));

	   // Set To: header field of the header
	   message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));

	   // Set Subject: header field
	   message.setSubject("TAG Verification Number");

	   // Set the actual message as HTML content
	   message.setContent("<h1>Your verification number is:</h1><p>" + generatedOTP + "</p>" + "<h2>Enter it to reset your password.<h2>", "text/html");

	   // Send message
	   Transport.send(message);
	   System.out.println("Email sent successfully with veritication number: " + generatedOTP);
	} catch (MessagingException mex) {
	   mex.printStackTrace();
	}
    }
}
