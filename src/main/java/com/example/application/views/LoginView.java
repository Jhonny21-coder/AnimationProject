package com.example.application.views;

// LoginView.java
import com.example.application.services.UserServices;
import com.example.application.data.User;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.UI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import com.example.application.views.comment.CommentView;

@Route("")
@PageTitle("Login | TAG")
public class LoginView extends VerticalLayout {

    private final UserServices userService;

    public LoginView(UserServices userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

	H1 text = new H1("Login");
	text.addClassName("login-text");

	Anchor forgotPassword = new Anchor("forgotPassword", "Forgot password?");
	forgotPassword.addClassName("forgot-password");

        // Create custom text fields for email and password
        EmailField emailField = new EmailField("Email");
	emailField.setErrorMessage("Enter a valid email address");
	emailField.setSuffixComponent(new Icon(VaadinIcon.ENVELOPE));

        PasswordField passwordField = new PasswordField("Password");
	emailField.addClassName("email");
	passwordField.addClassName("password");

        // Create login button
        Button loginButton = new Button("Login");

	loginButton.addClassName("button");
        loginButton.addClickListener(event -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();

	    System.out.println("\nIn Login");
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

            // Authenticate user using UserServices directly
            if (userService.authenticate(email, password)) {
                // Store user information in session
                VaadinSession.getCurrent().setAttribute("user", email);
                // Redirect to MainLayout
                getUI().ifPresent(ui -> ui.navigate(MainFeed.class));
                // Show notification
                Notification.show("Login successful!", 3000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Login failed. Please check your credentials.", 3000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Create registration link
        Anchor registerLink = new Anchor("register", "No account yet? Register here.");

        // Create FormLayout and add components
        FormLayout formLayout = new FormLayout();
	formLayout.add(emailField, passwordField, loginButton);
	// Add labels to the layout
        formLayout.setResponsiveSteps(
                // Use one column by default
                new ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2));
        // Stretch the username field over 2 columns
        formLayout.setColspan(emailField, 2);

        registerLink.addClassName("form-link");
        addClassName("form");
	getThemeList().set(Lumo.DARK, true);

	add(text, formLayout, forgotPassword, registerLink);
    }

    public static boolean isDataExisting(String filename, String data) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
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


