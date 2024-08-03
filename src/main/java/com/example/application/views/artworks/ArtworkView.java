package com.example.application.views.artworks;

import com.example.application.views.EmcView;
import com.example.application.repository.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.HasUrlParameter;
import com.example.application.data.*;
import com.example.application.services.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import java.util.List;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.*;
import java.util.concurrent.atomic.*;
import java.util.stream.Collectors;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.server.StreamResource;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;

@Route("artworkView")
public class ArtworkView extends AppLayout implements HasUrlParameter<Long> {

    private final ArtworkService artworkService;
    private final UserServices userService;
    private final LikeReactionService likeReactionService;
    private final HeartReactionService heartReactionService;
    private Span likeReactionsSpan = new Span();
    private Span heartReactionsSpan = new Span();

    public ArtworkView(ArtworkService artworkService, UserServices userService,
		LikeReactionService likeReactionService,
	 	HeartReactionService heartReactionService) {

        this.artworkService = artworkService;
	this.userService = userService;
	this.likeReactionService = likeReactionService;
	this.heartReactionService = heartReactionService;

        addClassName("artwork-main");
    }

    @Override
    public void setParameter(BeforeEvent event, Long userId) {
        List<Artwork> artworks = artworkService.getArtworksByUserId(userId);
        displayArtworks(userId, artworks);
    }

    private Button createLikeButton(Long like, User user, Artwork artwork){
    	List<LikeReaction> totalLikeReactions = likeReactionService.getReactionForArtworkId(artwork.getId());
	// Filter out the current user's reaction
    	totalLikeReactions = totalLikeReactions.stream()
            .filter(reaction -> !reaction.getUser().getId().equals(user.getId()))
            .collect(Collectors.toList());

	LikeReaction currentReaction = likeReactionService.getReactionByUserAndArtwork(user.getEmail(), artwork.getId());

    	AtomicBoolean userAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

    	Span totalLikeReactionsSpan = new Span();
    	totalLikeReactionsSpan.setText(String.valueOf(like));
    	totalLikeReactionsSpan.addClassName("reactions");

	List<LikeReaction> likeReactions = likeReactionService.getReactionForArtworkId(artwork.getId());

    	AtomicInteger likeReaction = new AtomicInteger(like.intValue());
	AtomicInteger totalLikes = new AtomicInteger(likeReactions.size());

    	Button likeButton = new Button(VaadinIcon.THUMBS_UP_O.create());
    	likeButton.addClickListener(event -> {
            if (!userAlreadyLiked.get()) {
            	likeReaction.incrementAndGet();

		likeReactionsSpan.setText(String.valueOf(likeReaction.get()));

            	likeReactionService.saveLikeReaction(user.getEmail(), artwork.getId(), (long) 1);

		totalLikes.incrementAndGet();

            	userAlreadyLiked.set(true); // Update the flag
            } else {
            	Notification.show("Sorry, you can only react once.", 3000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
    	});

    	likeButton.addClassName("like-button");

    	return likeButton;
    }

    private Button createHeartButton(Long heart, User user, Artwork artwork){
    	List<HeartReaction> totalHeartReactions = heartReactionService.getReactionForArtworkId(artwork.getId());

   	AtomicBoolean userAlreadyHearted = new AtomicBoolean(totalHeartReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

    	Span totalHeartReactionsSpan = new Span();
    	totalHeartReactionsSpan.setText(String.valueOf(heart));
    	totalHeartReactionsSpan.addClassName("reactions");

	List<LikeReaction> likeReactions = likeReactionService.getReactionForArtworkId(artwork.getId());

    	AtomicInteger heartReaction = new AtomicInteger(heart.intValue());

    	Button heartButton = new Button(VaadinIcon.HEART_O.create());
    	heartButton.addClickListener(event -> {
            if (!userAlreadyHearted.get()) {
            	heartReaction.incrementAndGet();

		heartReactionsSpan.setText(String.valueOf(heartReaction.get()));

            	heartReactionService.saveHeartReaction(user.getEmail(), artwork.getId(), (long) 1);

            	userAlreadyHearted.set(true); // Update the flag
            } else {
            	Notification.show("Sorry, you can only react once.", 3000, Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
    	});

    	heartButton.addClassName("like-button");

    	return heartButton;
    }

    private VerticalLayout getReactions(Artwork artwork, List<LikeReaction> likeReactions, List<HeartReaction> heartReactions) {
	HorizontalLayout reactionLayout = new HorizontalLayout();

    	User user = userService.findCurrentUser();

    	boolean userLiked = false;
    	boolean userHearted = false;

    	List<LikeReaction> totalLikeReactions = likeReactionService.getReactionForArtworkId(artwork.getId());
	List<HeartReaction> totalHeartReactions = heartReactionService.getReactionForArtworkId(artwork.getId());

    	for (LikeReaction likeReaction : likeReactions) {
            if (likeReaction.getUser().getId().equals(user.getId())) {
            	userLiked = true;
            	break;
            }
    	}

    	for (HeartReaction heartReaction : heartReactions) {
            if (heartReaction.getUser().getId().equals(user.getId())) {
            	userHearted = true;
            	break;
            }
    	}

  	Long likesPerArtwork = 0L;
  	for(LikeReaction likeReaction : totalLikeReactions){
      	   likesPerArtwork += likeReaction.getLikeReaction();
  	}

  	AtomicInteger totalLikes = new AtomicInteger(likesPerArtwork.intValue());

  	Span likes = new Span(String.valueOf(totalLikes.get()));

    	if (userLiked) {
            Button unlikeButton = new Button("Unlike");
	    unlikeButton.addClassName("unlike");
            unlikeButton.addClickListener(event -> {
            	// Remove the user's previous like reaction
            	likeReactionService.removeLikeReaction(user.getEmail(), artwork.getId());

	    	totalLikes.decrementAndGet();
	    	likes.setText(String.valueOf(totalLikes.get()));

		likeReactionsSpan.setText(String.valueOf(totalLikes.get()));
            	// Update UI to remove unlike button and add like button
            	reactionLayout.remove(unlikeButton);
            	Button likeButton = createLikeButton((long) totalLikes.get() - 1, user, artwork);
            	reactionLayout.add(likeButton);
            });
            reactionLayout.add(unlikeButton);
    	} else {
            Button likeButton = createLikeButton((long) totalLikes.get(), user, artwork);

            reactionLayout.add(likeButton);
    	}

	Long heartsPerArtwork = 0L;
        for(HeartReaction heartReaction : totalHeartReactions){
           heartsPerArtwork += heartReaction.getHeartReaction();
        }

        AtomicInteger totalHearts = new AtomicInteger(heartsPerArtwork.intValue());

        Span hearts = new Span(String.valueOf(totalHearts.get()));

    	if (userHearted) {
            Button unheartButton = new Button("Unheart");
	    unheartButton.addClassName("unheart");
            unheartButton.addClickListener(event -> {
            	// Remove the user's previous heart reaction
            	heartReactionService.removeHeartReaction(user.getEmail(), artwork.getId());

		totalHearts.decrementAndGet();
                hearts.setText(String.valueOf(totalHearts.get()));

		heartReactionsSpan.setText(String.valueOf(totalHearts.get()));

            	// Update UI to remove unheart button and add heart button
            	reactionLayout.remove(unheartButton);
            	Button heartButton = createHeartButton((long) heartReactions.size() - 1, user, artwork);
            	reactionLayout.add(heartButton);
            });
            reactionLayout.add(unheartButton);
    	} else {
            Button heartButton = createHeartButton((long) heartReactions.size(), user, artwork);

            reactionLayout.add(heartButton);
    	}

    	VerticalLayout mainLayout = new VerticalLayout(reactionLayout);
    	return mainLayout;
    }

    private long getTotalLikesForArtwork(Long artworkId) {
        List<LikeReaction> likeReactions = likeReactionService.getReactionForArtworkId(artworkId);
        return likeReactions.stream().mapToLong(LikeReaction::getLikeReaction).sum();
    }

    private long getTotalHeartsForArtwork(Long artworkId) {
        List<HeartReaction> heartReactions = heartReactionService.getReactionForArtworkId(artworkId);
        return heartReactions.stream().mapToLong(HeartReaction::getHeartReaction).sum();
    }

    private long getTotalLikes() {
        return artworkService.getAllArtworks().stream().mapToLong(artwork -> getTotalLikesForArtwork(artwork.getId())).sum();
    }

    private long getTotalHearts() {
        return artworkService.getAllArtworks().stream().mapToLong(artwork -> getTotalHeartsForArtwork(artwork.getId())).sum();
    }

    private void displayArtworks(Long userId, List<Artwork> artworks) {
    	// Fetch the user object based on the user ID
    	User user = userService.getUserById(userId);

    	// Create a header with the full name of the user
    	H1 fullName = new H1(user.getFirstName() + " artworks");
    	fullName.addClassName("fullname");

    	Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
    	backIcon.addClassName("back-icon");
    	backIcon.addClickListener(event -> {
           getUI().ifPresent(ui -> ui.navigate(EmcView.class));
    	});

    	//HorizontalLayout horizontal = new HorizontalLayout(backIcon, fullName);
    	//horizontal.setWidthFull();

    	//VerticalLayout vertical = new VerticalLayout(horizontal);

    	VerticalLayout layout = new VerticalLayout();

	FormLayout formLayout = new FormLayout();
	formLayout.add(layout);
	formLayout.setResponsiveSteps(new ResponsiveStep("0", 1),
	    new ResponsiveStep("500px", 3));

    	if(artworks != null){

       	  Dialog dialog = new Dialog();
          long totalLikes = 0L;
          long totalHearts = 0L;
          long artworkId = 0L;
          Artwork art = null;

          // Fetch like and heart reactions for all artworks outside of the loop
          Map<Long, List<LikeReaction>> likeReactionsMap = new HashMap<>();
          Map<Long, List<HeartReaction>> heartReactionsMap = new HashMap<>();

       	  for (Artwork artwork : artworks) {
          	likeReactionsMap.put(artwork.getId(), likeReactionService.getReactionForArtworkId(artwork.getId()));
          	heartReactionsMap.put(artwork.getId(), heartReactionService.getReactionForArtworkId(artwork.getId()));
          }

          for (Artwork artwork : artworks) {
          	artworkId = artwork.getId();
          	art = artwork;

            	List<LikeReaction> likeReactions = likeReactionsMap.get(artworkId);
            	List<HeartReaction> heartReactions = heartReactionsMap.get(artworkId);

            	// Calculate total likes and hearts for the current artwork
            	long likesForArtwork = getTotalLikesForArtwork(artworkId);
            	long heartsForArtwork = getTotalHeartsForArtwork(artworkId);

            	totalLikes += likesForArtwork;
            	totalHearts += heartsForArtwork;

		final String FILENAME = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

                Image artworkImage = new Image();
                //artworkImage.addClassName("artwork-image");

                try (FileInputStream userFis = new FileInputStream(FILENAME)) {
                    byte[] bytes = userFis.readAllBytes();

                    StreamResource resources = new StreamResource(artwork.getArtworkUrl(), () -> new ByteArrayInputStream(bytes));
                    artworkImage.setSrc(resources);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            	// Create components to represent each artwork
            	//Image artworkImage = new Image("./artwork_images/" + artwork.getArtworkUrl(), "Artwork");
            	//artworkImage.addClassName("artwork-image");

            	//H1 artworkDescription = new H1(artwork.getDescription());
            	//artworkDescription.addClassName("title");

            	String description = artwork.getDescription();

		if (description.length() > 37) {
           	    description = description.replaceAll("(.{37})", "$1\n");
        	}

        	H1 artworkDescription = new H1(description);
                artworkDescription.addClassName("title");

            	LocalTime localTime = artwork.getTimeOfPost();
            	LocalDate localDate = artwork.getDateOfPost();

            	// Define the format pattern for displaying the time and date
            	DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                    .withLocale(Locale.US);
            	DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    .withLocale(Locale.US);

            	// Format the LocalTime and LocalDate into strings using the defined patterns
            	String formattedTime = timeFormatter.format(localTime);
            	String formattedDate = dateFormatter.format(localDate);

            	// Combine the formatted time and date strings
            	String formattedDateTime = formattedDate + ", " + formattedTime;

            	Span postedDate = new Span("Posted on " + formattedDateTime);
            	postedDate.addClassName("posted");

		Div seperator = new Div();
                seperator.addClassName("seperator");

		if (artwork.getArtworkUrl().equals(artworks.get(artworks.size() - 1).getArtworkUrl())) {
                    seperator.setVisible(false);
                }

            	VerticalLayout reactionLayout = getReactions(art, likeReactions, heartReactions);

            	formLayout.add(artworkDescription, postedDate, artworkImage, reactionLayout, seperator);
	   }

       	   // Update total likes and hearts
           likeReactionsSpan.setText(String.valueOf(totalLikes));
           likeReactionsSpan.addClassName("totalreactions-span");

           heartReactionsSpan.setText(String.valueOf(totalHearts));
           heartReactionsSpan.addClassName("heartreactions-span");

           Icon likeIcon = new Icon(VaadinIcon.THUMBS_UP);
           likeIcon.addClassName("likeicon");

           Icon heartIcon = new Icon(VaadinIcon.HEART);
           heartIcon.addClassName("hearticon");

           HorizontalLayout reactionsLayout = new HorizontalLayout(likeIcon, likeReactionsSpan, heartIcon, heartReactionsSpan);

           // Add the reactions layout to the UI
          // addToNavbar(vertical);
           addToNavbar(backIcon, fullName);
           addToNavbar(true, reactionsLayout);
	} else {
       	   H2 h2 = new H2("No Artwork Yet");
       	   layout.add(h2);
    	}

    	layout.setWidthFull();
    	layout.addClassName("artwork-layout");
    	//setContent(layout);
    	setContent(formLayout);
    }
}

