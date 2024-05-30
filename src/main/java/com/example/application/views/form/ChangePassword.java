package com.example.application.views.form;

import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

@Route("changePassword")
public class ChangePassword extends VerticalLayout {
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserServices userService;

    private FormLayout currentPasswordForm = new FormLayout();
    private FormLayout newPasswordForm = new FormLayout();
    private FormLayout confirmPasswordForm = new FormLayout();

    private TextField passwordField = new TextField("Enter your current password");
    private TextField newPasswordField = new TextField("Enter your new password");
    private TextField confirmPassword = new TextField("Confirm your new password");

    private Button sendButton = new Button("Send");
    private Button cancelButton = new Button("Cancel");
    private Button saveButton = new Button("Save");
    private Button backButton = new Button("Back");
    private Button changeBackButton = new Button("Back");
    private Button nextButton = new Button("Next");

    private Icon checkIcon;

    private Span passwordStrengthText;
    private H3 currentText = new H3("Enter existing password");
    private H3 newPasswordText = new H3("Create new password");
    private H3 confirmPasswordText = new H3("Confirm new password");

    public ChangePassword(UserServices userService){
    	this.userService = userService;
	addClassName("form");

	setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

	saveButton.addClassName("save");
	backButton.addClassName("save");
	changeBackButton.addClassName("save");
	nextButton.addClassName("save");
	newPasswordField.addClassName("register-field");
	passwordField.addClassName("register-field");

	currentPassword();

    }

    public void currentPassword(){
	currentText.getStyle().set("font-family", "serif")
		.set("color", "white");

    	cancelButton.addClassName("close");
	cancelButton.addClickListener(event -> {
	   getUI().ifPresent(ui -> ui.navigate("emc-view"));
	});

    	sendButton.addClassName("save");
    	sendButton.addClickListener(event -> {
	   String passwordValue = passwordField.getValue();

	   User user = userService.findUserByPassword(passwordValue);

	   if(user == null){
	      Notification.show("Incorrect password. Please try again", 3000, Notification.Position.TOP_CENTER)
	   	.addThemeVariants(NotificationVariant.LUMO_ERROR);
	   }else{
	      changePassword(user);
	   }
	});

	currentPasswordForm.add(passwordField, 2);
	currentPasswordForm.add(sendButton, cancelButton);

    	remove(newPasswordText, confirmPasswordText, newPasswordForm, confirmPasswordForm);
    	add(currentText, currentPasswordForm);
    }

    private void changePassword(User user){
    	newPasswordText.getStyle().set("font-family", "serif")
            .set("color", "white");

	nextButton.addClickListener(event -> {
	    String newPasswordValue = newPasswordField.getValue();
	    String currentPassword = user.getPassword();

	    if(passwordEncoder.matches(newPasswordValue, currentPassword)){
	    	Notification.show("Old password cannot be new password", 3000, Notification.Position.TOP_CENTER)
	    		.addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }else if(newPasswordValue.isEmpty()){
	    	Notification.show("Password cannot be blank", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
	    }else{
	    	passwordConfirmation(user);
	    }
	});

	changeBackButton.addClickListener(event -> {
	    currentPassword();
	});

	newPasswordForm.add(newPasswordField, 2);
	newPasswordForm.add(nextButton, changeBackButton);

	remove(currentText, confirmPasswordText, currentPasswordForm, confirmPasswordForm);
	add(newPasswordText, newPasswordForm);
    }

    private void passwordConfirmation(User user){
	saveButton.setVisible(false);

	confirmPasswordText.getStyle().set("font-family", "serif")
                .set("color", "white");

    	Div passwordStrength = new Div();
        passwordStrengthText = new Span();
        passwordStrength.add(passwordStrengthText);
        confirmPassword.setHelperComponent(passwordStrength);

        confirmPassword.addClassName("register-field");
        confirmPassword.setValueChangeMode(ValueChangeMode.EAGER);
        confirmPassword.addValueChangeListener(e -> {
            String password = newPasswordField.getValue();
            String newPassword = e.getValue();

            updateHelper(password, newPassword);

        });

        checkIcon = VaadinIcon.CHECK.create();
        checkIcon.getStyle().set("color", "var(--lumo-success-color)");
        checkIcon.setVisible(false);

        // Set checkIcon as the suffix component of confirmPassword field
        confirmPassword.setSuffixComponent(checkIcon);

	backButton.addClickListener(event -> {
	    changePassword(user);
	});

	saveButton.addClickListener(event -> {
	    ConfirmDialog dialog = new ConfirmDialog();
            dialog.addClassName("view-dialog");
            dialog.open();
            dialog.setCancelable(true);
            dialog.setConfirmText("Save");
            dialog.setConfirmButtonTheme("primary");
            dialog.setHeader("Are you sure you want to change your password?");
            dialog.addConfirmListener(events -> {
	    	String newPassword = confirmPassword.getValue();
	    	String encryptedPassword = passwordEncoder.encode(newPassword);

	    	user.setPassword(encryptedPassword);

	    	userService.updateUser(user);

	    	Notification.show("Password updated successfully", 3000, Notification.Position.TOP_CENTER)
	    	    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	    	getUI().ifPresent(ui -> ui.navigate("emc-view"));
	    });
	});

        updateHelper("", "");

        confirmPasswordForm.add(confirmPassword, 2);
        confirmPasswordForm.add(saveButton, backButton);

	remove(newPasswordText, currentText, newPasswordForm, currentPasswordForm);
	add(confirmPasswordText, confirmPasswordForm);
    }

    private void updateHelper(String password, String newPassword) {
        if (password.equals(newPassword) && !newPassword.isEmpty() && !password.isEmpty()) {
            passwordStrengthText.setText("Password matched");
            passwordStrengthText.getStyle().set("color", "var(--lumo-success-color)");
            checkIcon.setVisible(true);
            saveButton.setVisible(true);
        } else if(!password.equals(newPassword) && !newPassword.isEmpty()){
            passwordStrengthText.setText("Password did not match");
            passwordStrengthText.getStyle().set("color", "var(--lumo-error-color)");
            checkIcon.setVisible(false);
            saveButton.setVisible(false);
        }else if(!password.isEmpty() && newPassword.isEmpty()){
            passwordStrengthText.setText("");
            checkIcon.setVisible(false);
            saveButton.setVisible(false);
        }
    }
}
