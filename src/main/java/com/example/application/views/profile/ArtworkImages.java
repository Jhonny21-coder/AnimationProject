package com.example.application.views.profile;

import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.data.Artwork;
import com.example.application.services.ArtworkService;
import com.example.application.data.LikeReaction;
import com.example.application.services.LikeReactionService;
import com.example.application.data.HeartReaction;
import com.example.application.services.HeartReactionService;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.UI;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;

@Route("arrwork-images")
public class ArtworkImages extends AppLayout implements HasUrlParameter<Long> {

    private final ArtworkService artworkService;
    private final UserServices userService;
    private final LikeReactionService likeService;
    private final HeartReactionService heartService;

    public ArtworkImages(ArtworkService artworkService, UserServices userService,
    	LikeReactionService likeService, HeartReactionService heartService){
        this.artworkService = artworkService;
        this.userService = userService;
        this.likeService = likeService;
        this.heartService = heartService;

        addClassName("profile-app-layout");
    }

     @Override
    public void setParameter(BeforeEvent event, Long userId){
    	User user = userService.getUserById(userId);

        createImagesLayout(user);
        createHeader(user);
    }

    private void createImagesLayout(User user) {
    	List<Artwork> artworks = artworkService.getArtworksByUserId(user.getId());

    	FormLayout formLayout = new FormLayout();

    	VerticalLayout layout = new VerticalLayout();
	layout.addClassName("column-layout");

	if(artworks.isEmpty()){
	   Span emptyText = new Span("No posted artworks");
	   emptyText.addClassName("no-posted-artworks");
	   layout.add(emptyText);
	}
    	// Create a new HorizontalLayout to start with
    	HorizontalLayout rowLayout = new HorizontalLayout();
    	rowLayout.addClassName("row-layout");

    	layout.add(rowLayout);

    	for (int i = 0; i < artworks.size(); i++) {
            Artwork artwork = artworks.get(i);

            Image image = new Image();
            image.addClassName("images-picture");

            Div imageDiv = new Div(image);
            imageDiv.addClassName("wrapped-images-picture");
            imageDiv.addClickListener(event -> {
                UI.getCurrent().navigate(ViewSpecificArtwork.class, artwork.getId());
            });

            String imageFile = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

            try (FileInputStream fis = new FileInputStream(imageFile)) {
            	byte[] bytes = fis.readAllBytes();

            	StreamResource resource = new StreamResource(artwork.getArtworkUrl(), () -> new ByteArrayInputStream(bytes));

            	image.setSrc(resource);
            } catch (Exception e) {
            	e.printStackTrace();
            }

	    Div div = createTotalReactions(artwork);
	    div.addClassName("total-div");

	    VerticalLayout totalLayout = new VerticalLayout(imageDiv, div);
	    totalLayout.addClassName("total-layout");

            // Add the image to the current row
            rowLayout.add(totalLayout);

            // If the current row has 2 images, or if this is the last image,
            // start a new row
            if ((i + 1) % 2 == 0 || i == artworks.size() - 1) {
            	rowLayout = new HorizontalLayout();
            	layout.add(rowLayout);
            }
	}

	Span text = new Span("Posted Artworks");
	text.addClassName("posted-artworks");

    	formLayout.add(new HorizontalLayout(text), layout);

    	setContent(formLayout);
    }

    private String formatValue(long value) {
        if (value >= 1_000_000) {
            return formatMillions(value);
        } else if (value >= 1_000) {
            return formatThousands(value);
        } else {
            return String.valueOf(value);
        }
    }

    private String formatMillions(long value) {
        String wrapped = String.valueOf(value);
        int length = wrapped.length();
        int significantDigits = length - 6; // Determine significant digits for millions

        if (wrapped.length() > significantDigits + 1 && wrapped.charAt(significantDigits) == '0') {
            return wrapped.substring(0, significantDigits) + "M";
        } else {
            return wrapped.substring(0, significantDigits) + "." + wrapped.charAt(significantDigits) + "M";
        }
    }

    private String formatThousands(long value) {
        String wrapped = String.valueOf(value);
        int length = wrapped.length();
        int significantDigits = length - 3; // Determine significant digits for thousands

        if (wrapped.length() > significantDigits + 1 && wrapped.charAt(significantDigits) == '0') {
            return wrapped.substring(0, significantDigits) + "K";
        } else {
            return wrapped.substring(0, significantDigits) + "." + wrapped.charAt(significantDigits) + "K";
        }
    }

    private Div createTotalReactions(Artwork artwork){
    	List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());
        List<HeartReaction> totalHeartReactions = heartService.getReactionForArtworkId(artwork.getId());

	Icon likeIcon = new Icon(VaadinIcon.THUMBS_UP);
        likeIcon.addClassName("images-like-icon");

        Icon heartIcon = new Icon(VaadinIcon.HEART);
        heartIcon.addClassName("images-heart-icon");

	Span totalLikes = new Span();
        totalLikes.setText(formatValue(totalLikeReactions.size() + 999995));
        totalLikes.addClassName("images-total-text");

        Span totalHearts = new Span();
        totalHearts.setText(formatValue(totalHeartReactions.size() + 999995));
        totalHearts.addClassName("images-total-text");

	return new Div(likeIcon, totalLikes, heartIcon, totalHearts);
    }

    private void createHeader(User user){
    	Icon backButton = new Icon(VaadinIcon.ARROW_BACKWARD);
        backButton.addClassName("profile-back-button");
        backButton.addClickListener(event -> {
             UI.getCurrent().navigate(UserProfile.class, user.getId());
        });

        Span fullName = new Span(user.getFullName());
        fullName.addClassName("view-fullname");

        addToNavbar(new HorizontalLayout(backButton, fullName));
    }
}
