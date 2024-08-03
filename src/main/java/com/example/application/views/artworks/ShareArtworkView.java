package com.example.application.views.artworks;

import com.example.application.data.User;
import com.example.application.data.Artwork;
import com.example.application.services.ArtworkService;
import com.example.application.views.LoginView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.avatar.Avatar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Locale;

@Route("shared-artwork")
public class ShareArtworkView extends AppLayout implements HasUrlParameter<Long>{

    private final ArtworkService artworkService;

    public ShareArtworkView(ArtworkService artworkService){
    	this.artworkService = artworkService;

    	addClassName("artwork-main");
    	navigationBar();
    }

    @Override
    public void setParameter(BeforeEvent event, Long artworkId) {
    	Artwork artwork = artworkService.getArtworkById(artworkId);

        displayArtwork(artwork);
    }

    private void displayArtwork(Artwork artwork){
    	FormLayout formLayout = new FormLayout();

    	Image image = new Image();
	image.addClassName("share-image");

	String artworkUrl = artwork.getArtworkUrl();

    	String sharedUrl = "src/main/resources/META-INF/resources/artwork_images/" + artworkUrl;

        try (FileInputStream currentFis = new FileInputStream(sharedUrl)) {
            byte[] currentBytes = currentFis.readAllBytes();

            StreamResource currentResources = new StreamResource(artworkUrl, () -> new ByteArrayInputStream(currentBytes));
            image.setSrc(currentResources);
        } catch (Exception e) {
            e.printStackTrace();
        }

	Span posted = createDateTimePosted(artwork);
	posted.addClassName("share-posted");

	String description = artwork.getDescription();

        if (description.length() > 37) {
           description = description.replaceAll("(.{37})", "$1\n");
        }

	Span title = new Span(description);
        title.addClassName("share-title");

	User user = artwork.getUser();
	Span name = new Span("Shared by " + user.getFullName());
	name.addClassName("share-name");

	VerticalLayout layout = new VerticalLayout(title, posted);

	Div nameDiv = new Div(name);
	nameDiv.addClassName("name-div");

	formLayout.add(layout, image, nameDiv);

        setContent(formLayout);
    }

    public Span createDateTimePosted(Artwork artwork){
    	LocalTime localTime = artwork.getTimeOfPost();
        LocalDate localDate = artwork.getDateOfPost();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
             .withLocale(Locale.US);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
             .withLocale(Locale.US);

        String formattedTime = timeFormatter.format(localTime);
        String formattedDate = dateFormatter.format(localDate);

        return new Span(formattedDate + " " + formattedTime);
    }

    private void navigationBar(){
    	Button loginButton = new Button("Login", new Icon(VaadinIcon.SIGN_IN));
	loginButton.addClassName("login-button");
	loginButton.addClickListener(event -> {
	     getUI().ifPresent(ui -> ui.navigate(LoginView.class));
	});

	Span headerText = new Span("Experience and enjoy TAG now");
	headerText.addClassName("share-header-text");

	Avatar avatar = new Avatar();
	avatar.setImage("./icons/icon.png");

	HorizontalLayout layout = new HorizontalLayout(loginButton, headerText, avatar);
	layout.addClassName("share-header-layout");

    	addToNavbar(layout);
    }
}
