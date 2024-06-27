package com.example.application.views;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.html.Anchor;

@Route("wait")
public class Wait extends VerticalLayout {

    private Anchor messengerLink = new Anchor("https://m.me/jhonnycoder", "Click Me");
    private H1 text = new H1("Can I wait?");
    private Span span = new Span("This is my first time making this, and I'm shy about telling to you directly. So I did this instead.");
    private Button yesButton = new Button("Yes", new Icon(VaadinIcon.CHECK));
    private Button noButton = new Button("No", new Icon(VaadinIcon.CLOSE));

    public Wait(){
    	setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("register-form");
        messengerLink.addClassName("messenger-link");

        waitLayout();
    }

    private void waitLayout(){
    	//H1 text = new H1("Can I wait?");
	text.getStyle().set("font-family", "serif")
	   .set("color", "white");

	//Span span = new Span("This is my first time making this, and I'm shy about telling to you directly. So I did this instead.");

	//Button yesButton = new Button("Yes", new Icon(VaadinIcon.CHECK));
	yesButton.addClassName("save");

	yesButtonListener(yesButton);

	//Button noButton = new Button("No", new Icon(VaadinIcon.CLOSE));
	noButton.addClassName("save");
	noButton.addClickListener(event -> {
	    System.out.println("Diana click no");
	    Notification.show("It's okay, I understand.", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	});

	messengerLink.setVisible(false);
	add(messengerLink, text, span, new HorizontalLayout(yesButton, noButton));
    }

    private void yesButtonListener(Button yesButton){
    	ConfirmDialog dialog1 = new ConfirmDialog();
    	dialog1.addClassName("wait-dialog");
    	dialog1.setConfirmText("Yes");
    	dialog1.setCancelable(true);
    	dialog1.setHeader("Thank you for clicking Yes");
    	dialog1.setText("Is it possible for me to wait until you might consider giving me even just a small chance?");

    	yesButton.addClickListener(event -> {
    	     System.out.println("Diana clicked yes");
    	     dialog1.open();
    	});

	ConfirmDialog dialog2 = new ConfirmDialog();
        dialog2.addClassName("wait-dialog");
        dialog2.setConfirmText("Next");
        dialog2.setCancelable(false);
        dialog2.setHeader("Thank youuu, Diana");
        dialog2.setText("Thanks for the sliver of hope you've offered me. It means a lot.");

    	dialog1.addConfirmListener(event -> {
    	      System.out.println("Diana clicked another yes");
    	      dialog2.open();
    	});

    	ConfirmDialog dialog3 = new ConfirmDialog();
        dialog3.addClassName("wait-dialog");
        dialog3.setConfirmText("Close");
        dialog3.setCancelable(false);
        dialog3.setHeader("Thank youuu, Diana");
        dialog3.setText("I promise to cherish it and wait for that time.");

        dialog2.addConfirmListener(event -> {
              System.out.println("Diana clicked another Next");
              dialog3.open();
        });

        dialog3.addConfirmListener(event -> {
              System.out.println("Diana clicked another close");
              Notification.show("Thank youuuu Diana <3", 3000, Notification.Position.MIDDLE)
              	.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

	      text.setVisible(false);
	      span.setVisible(false);
	      yesButton.setVisible(false);
	      noButton.setVisible(false);
              messengerLink.setVisible(true);
        });
    }
}
