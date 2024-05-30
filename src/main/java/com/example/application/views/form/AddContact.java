package com.example.application.views.form;

import com.example.application.views.MainLayout;
import com.example.application.data.Contact;
import com.example.application.services.ContactService;
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
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.InputStreamFactory;

@Route("addContact")
public class AddContact extends AppLayout {

    private final ContactService contactService;
    private final UserServices userService;
    private final TextField email = new TextField("Email Account");
    private final TextField phoneNumber = new TextField("Phone Number");
    private final TextField facebook = new TextField("Facebook Account");
    private final TextField instagram = new TextField("Instagram Account");
    private final TextField tiktok = new TextField("Tiktok Account");
    private final Button save = new Button("Save");
    private final Button close = new Button("Close");

    public AddContact(ContactService contactService, UserServices userService) {
        this.contactService = contactService;
        this.userService = userService;

	Image instagramIcon = new Image("./icons/instagram.svg", "Instagram Icon");
	Image tiktokIcon = new Image("./icons/tiktok.svg", "Tiktok Icon");
	Image phoneIcon = new Image("./icons/phone.svg", "Phone Icon");
	Image facebookIcon = new Image("./icons/facebook.svg", "Facebook Icon");

	email.setSuffixComponent(new Icon(VaadinIcon.ENVELOPE_O));
        phoneNumber.setSuffixComponent(phoneIcon);
	facebook.setSuffixComponent(facebookIcon);
        instagram.setSuffixComponent(instagramIcon);
	tiktok.setSuffixComponent(tiktokIcon);

        createHeader();

	phoneNumber.setPlaceholder("+63 000 000 0000");

        addClassName("register-form");

        email.addClassName("register-field");
        phoneNumber.addClassName("register-field");
        facebook.addClassName("register-field");
	instagram.addClassName("register-field");
	tiktok.addClassName("register-field");
	save.addClassName("save");
        close.addClassName("close");

        User user = userService.findCurrentUser();

        save.addClickListener(event -> {
            if (user != null) {
                String userEmail = user.getEmail();
                String newEmailValue = email.getValue();
                String phoneNumberValue = phoneNumber.getValue();
                String facebookValue = facebook.getValue();
                String instagramValue = instagram.getValue();
                String tiktokValue = tiktok.getValue();

                if(newEmailValue != null && phoneNumberValue != null && facebookValue != null && instagramValue != null && tiktokValue != null){
                   contactService.saveContact(userEmail, newEmailValue,phoneNumberValue, facebookValue, instagramValue, tiktokValue);

                   Notification.show("Contact saved successfully", 3000, Notification.Position.TOP_CENTER)
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
        formLayout.add(email, phoneNumber, facebook, instagram, tiktok, new HorizontalLayout(save, close));
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 3));
        addClassName("addstudent-layout");

        setContent(formLayout);
    }

    private void createHeader(){
        H1 welcome = new H1("Add Contact Information");
        welcome.addClassName("welcome");

        Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("back-icon");
        backIcon.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("emc-view"));
        });

        HorizontalLayout header = new HorizontalLayout(backIcon, welcome);
        header.setWidthFull();

        addToNavbar(header);
    }
}
