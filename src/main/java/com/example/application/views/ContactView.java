package com.example.application.views;

import com.example.application.views.form.AddContact;
import com.example.application.data.*;
import com.example.application.services.*;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.details.Details;

import java.util.List;

@Route("contact")
public class ContactView extends AppLayout {

    private final ContactService contactService;
    private final UserServices userService;

	    public ContactView(ContactService contactService, UserServices userService){
	this.contactService = contactService;
	this.userService = userService;
	addClassName("acessinfo-main");

	HorizontalLayout footer = createFooter();

	User user = userService.findCurrentUser();
	Contact contact = contactService.getContactByUserId(user.getId());

        VerticalLayout contactContent = new VerticalLayout();

        Div divider2 = new Div();
        divider2.setHeight("1px");
        divider2.addClassName("divider2");

        if(contact != null) {
           Span email = new Span("Email: " + contact.getEmail());
           Span phoneNumber = new Span("Phone Number: " + contact.getPhoneNumber());
           Span facebook = new Span("Facebook: " + contact.getFacebook());
           Span instagram = new Span("Instagram: " + contact.getInstagram());
           Span tiktok = new Span("Tiktok: " + contact.getTiktok());

           email.addClassName("access-details");
           phoneNumber.addClassName("access-details");
           facebook.addClassName("access-details");
           instagram.addClassName("access-details");
           tiktok.addClassName("access-details");
           contactContent.add(email, phoneNumber, facebook, instagram, tiktok);
        }else{
           Span noInfoYet = new Span("No contact information, click the button below to add your contact information.");
           noInfoYet.addClassName("noinfo-span");
           contactContent.add(noInfoYet);
        }

        Details contactDetails = new Details("Contact Information", contactContent);
        contactDetails.setOpened(false);
        contactDetails.addClassName("contact-details");

        Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("back-icon");
        backIcon.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(EmcView.class));
        });

	H1 welcome = new H1("Contact Information");
        welcome.addClassName("welcome");

        HorizontalLayout header = new HorizontalLayout(backIcon, welcome);
        header.setWidthFull();

        VerticalLayout content = new VerticalLayout(contactDetails);

        addToNavbar(header);
        addToNavbar(true, footer);

        setContent(content);
    }

    private HorizontalLayout createFooter(){
        RouterLink editLink = new RouterLink();
        Div addLink = new Div();
	addLink.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(AddContact.class)));

        editLink.addClassName("contact-link");
        addLink.addClassName("contact-link");

        editLink.setRoute(ContactView.class);
        //addLink.setRoute(ContactView.class);

        Icon editIcon = new Icon(VaadinIcon.TOUCH);
        editIcon.addClassName("edit-icon");

        Icon addIcon = new Icon(VaadinIcon.PLUS_CIRCLE_O);
        addIcon.addClassName("add-contact-icon");

        Icon editContactIcon = new Icon(VaadinIcon.TOUCH);
        editContactIcon.addClassName("edit-contact-icon");

        Span editContactSpan = new Span("Edit Contact Information");
        editContactSpan.addClassName("edit-contact-text");

        Span addSpan = new Span("Add Contact Information");
        addSpan.addClassName("add-contact-text");

        User user = userService.findCurrentUser();
	Contact contact = contactService.getContactByUserId(user.getId());

        if(contact == null){
           addLink.add(addIcon, addSpan);
        }else {
           addLink.add(editContactIcon, editContactSpan);
        }

        HorizontalLayout footer = new HorizontalLayout(addLink);
        footer.setWidthFull();

        return footer;
    }
}
