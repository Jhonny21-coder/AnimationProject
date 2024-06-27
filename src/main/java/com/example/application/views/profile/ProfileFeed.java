package com.example.application.views.profile;

import com.example.application.data.LikeReaction;
import com.example.application.services.LikeReactionService;
import com.example.application.data.HeartReaction;
import com.example.application.services.HeartReactionService;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.data.Artwork;
import com.example.application.services.ArtworkService;
import com.example.application.data.Comment;
import com.example.application.services.CommentService;
import com.example.application.views.profile.UserProfile;
import com.example.application.views.EmcView;
import com.example.application.views.comment.CommentSectionView;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.UI;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Locale;
import java.util.List;
import java.util.Set;

import java.text.DecimalFormat;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import com.vaadin.flow.server.VaadinSession;

public class ProfileFeed extends AppLayout {

    private final ArtworkService artworkService;
    private final LikeReactionService likeService;
    private final HeartReactionService heartService;
    private final CommentService commentService;
    private final UserServices userService;

    public ProfileFeed(ArtworkService artworkService, LikeReactionService likeService,
    	HeartReactionService heartService, CommentService commentService, UserServices userService){

    	this.artworkService = artworkService;
    	this.likeService = likeService;
    	this.heartService = heartService;
    	this.commentService = commentService;
	this.userService = userService;

    	addClassName("main-feed");
    }

    public FormLayout createFeed(User userr){
    	List<Artwork> artworks = artworkService.getArtworksByUserId(userr.getId());

	FormLayout formLayout = new FormLayout();

    	for(Artwork artwork : artworks){
	    Image image = new Image();
    	    image.addClassName("main-feed-image");

    	    String artworkUrl = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

            try (FileInputStream userFis = new FileInputStream(artworkUrl)) {
            	byte[] userBytes = userFis.readAllBytes();

            	StreamResource userResource = new StreamResource(artwork.getArtworkUrl(), () -> new ByteArrayInputStream(userBytes));

            	image.setSrc(userResource);
            } catch (Exception e) {
            	e.printStackTrace();
            }

	    List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());
	    List<HeartReaction> totalHeartReactions = heartService.getReactionForArtworkId(artwork.getId());

	    User user = artwork.getUser();
	    User currentUser = userService.findCurrentUser();

	    Span totalReactions = new Span();
	    totalReactions.addClassName("reacted");

	    Span commented = new Span();
	    commented.addClassName("commented");

            Button likeButton = createLikeButtonListener(totalLikeReactions, currentUser, artwork, totalReactions);
	    Button heartButton = createHeartButtonListener(totalHeartReactions, currentUser, artwork, totalReactions);
	    Button commentButton = createCommentButtonListener(user, artwork);

	    Icon shareButton = new Icon(VaadinIcon.LINK);
            shareButton.addClassName("feed-share");

	    HorizontalLayout buttonsLayout = new HorizontalLayout(likeButton, heartButton, commentButton, shareButton);
            buttonsLayout.addClassName("main-feed-buttons");

	    HorizontalLayout profileLayout = createFeedHeader(user, artwork.getId());
	    profileLayout.addClassName("profile-layout2");

	    Button datePosted = createDateTimePosted(artwork);
            datePosted.addClassName("feed-date-posted");

	    HorizontalLayout totalReactionsDiv = createTotalReactions(totalLikeReactions, totalHeartReactions, totalReactions, commented, artwork);

	    formLayout.add(profileLayout, datePosted, image, totalReactionsDiv, buttonsLayout);
    	}

    	return formLayout;
    }

    public HorizontalLayout createTotalReactions(List<LikeReaction> totalLikeReactions, List<HeartReaction> totalHeartReactions, Span totalReactions, Span commented, Artwork artwork){
    	Icon likeIcon = new Icon(VaadinIcon.THUMBS_UP);
        likeIcon.addClassName("reactions-like");

        Icon heartIcon = new Icon(VaadinIcon.HEART);
        heartIcon.addClassName("reactions-heart");

        Icon happyIcon = new Icon(VaadinIcon.SMILEY_O);
        happyIcon.addClassName("reactions-happy");

        DecimalFormat formatter = new DecimalFormat("#,##");

        Long totals = (long) totalLikeReactions.size() + totalHeartReactions.size();

        totalReactions.setText(formatter.format(totals) + " reactions");

	List<Comment> comments = commentService.getCommentsByArtworkId(artwork.getId());
	commented.setText(formatValue((long) comments.size()) + " comments");

        HorizontalLayout reactionsDiv = new HorizontalLayout(likeIcon, heartIcon, happyIcon, commented, totalReactions);
        reactionsDiv.addClassName("comment-reactions-div");

        return reactionsDiv;
    }

    private HorizontalLayout createFeedHeader(User user, Long artworkId){
    	Span name = new Span(user.getFullName());
        name.addClassName("feed-name");

        Avatar avatar = new Avatar();
	avatar.addClassName("feed-avatar");
	avatar.getStyle().set("pointer-events", "none");

	String avatarFile = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

	try (FileInputStream fis = new FileInputStream(avatarFile)) {
             byte[] bytes = fis.readAllBytes();

             StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

             avatar.setImageResource(resource);
	} catch (Exception e) {
             e.printStackTrace();
	}

	Div avatarDiv = new Div(avatar);

	return new HorizontalLayout(avatarDiv, name);
    }

    public Button createCommentButtonListener(User user, Artwork artwork){
    	List<Comment> comments = commentService.getCommentsByArtworkId(artwork.getId());

	Long totalComments = (long) comments.size();

    	Button commentButton = new Button();
	commentButton.addClassName("feed-comment");
	commentButton.setIcon(new Icon(VaadinIcon.COMMENT_ELLIPSIS_O));
	commentButton.setText(formatValue(totalComments));
	commentButton.addClickListener(event -> {
	    Set<String> sessionNames = VaadinSession.getCurrent().getSession().getAttributeNames();

	    for(String sessionName : sessionNames){
	    	if(sessionName.equals("main")){
	    	   VaadinSession.getCurrent().getSession().removeAttribute("main");
	    	}
	    }

	    Long artworkId = artwork.getId();
            VaadinSession.getCurrent().getSession().setAttribute("profile", artworkId);

            UI.getCurrent().navigate(CommentSectionView.class, artworkId);
	});

	return commentButton;
    }

    /* Start format number */
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
    /* End format number */

    public Button createLikeButtonListener(List<LikeReaction> totalLikeReactions, User user, Artwork
	artwork, Span totalReactions){

	DecimalFormat formatter = new DecimalFormat("#,##");

	Button likeButton = new Button();
	likeButton.addClassName("feed-button");
        likeButton.addClassName("feed-like");

	likeButton.setText(formatValue((long) totalLikeReactions.size()));

    	AtomicBoolean userAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

        if(userAlreadyLiked.get()){
           likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
        }else{
           likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
        }

	AtomicLong atomicLong = new AtomicLong((long) totalLikeReactions.size());

        likeButton.addClickListener(event -> {
            handleClickListener(userAlreadyLiked, atomicLong, user, artwork, likeButton, totalReactions, "Like");
        });

	return likeButton;
    }

    public Button createHeartButtonListener(List<HeartReaction> totalHeartReactions, User user, Artwork
        artwork, Span totalReactions){

        DecimalFormat formatter = new DecimalFormat("#,##");

        Button heartButton = new Button();
        heartButton.addClassName("feed-heart");

        heartButton.setText(formatValue((long) totalHeartReactions.size()));

        AtomicBoolean userAlreadyHearted = new AtomicBoolean(totalHeartReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

        if(userAlreadyHearted.get()){
           heartButton.setIcon(new Icon(VaadinIcon.HEART));
        }else{
           heartButton.setIcon(new Icon(VaadinIcon.HEART_O));
        }

        AtomicLong atomicLong = new AtomicLong((long) totalHeartReactions.size());

        heartButton.addClickListener(event -> {
             handleClickListener(userAlreadyHearted, atomicLong, user, artwork, heartButton, totalReactions, "Heart");
         });

         return heartButton;
    }

    private void handleClickListener(AtomicBoolean isAlreadyReacted, AtomicLong atomicLong, User user, Artwork artwork,
        Button button, Span totalReactions, String reactType){

        if(isAlreadyReacted.get()){
           atomicLong.decrementAndGet();

           if(reactType.equals("Like")){
              likeService.removeLikeReaction(user.getEmail(), artwork.getId());

              button.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
           }else if(reactType.equals("Heart")){
              heartService.removeHeartReaction(user.getEmail(), artwork.getId());

              button.setIcon(new Icon(VaadinIcon.HEART_O));
           }

           button.setText(formatValue(atomicLong.get()));

           List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
           List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

           Long totals = (long) totalLikes.size() + totalHearts.size();

           totalReactions.setText(formatValue(totals) + " reactions");
           isAlreadyReacted.set(false);
        }else{
           atomicLong.incrementAndGet();

           if(reactType.equals("Like")){
              likeService.saveLikeReaction(user.getEmail(), artwork.getId(), (long) 1);

              button.setIcon(new Icon(VaadinIcon.THUMBS_UP));
           }else if(reactType.equals("Heart")){
              heartService.saveHeartReaction(user.getEmail(), artwork.getId(), (long) 1);

              button.setIcon(new Icon(VaadinIcon.HEART));
           }

           button.setText(formatValue(atomicLong.get()));

           List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
           List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

           Long totals = (long) totalLikes.size() + totalHearts.size();

           totalReactions.setText(formatValue(totals) + " reactions");

           isAlreadyReacted.set(true);
        }
     }

    private Button createDateTimePosted(Artwork artwork){
        LocalTime localTime = artwork.getTimeOfPost();
        LocalDate localDate = artwork.getDateOfPost();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
             .withLocale(Locale.US);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
             .withLocale(Locale.US);

        String formattedTime = timeFormatter.format(localTime);
        String formattedDate = dateFormatter.format(localDate);

        String dateTime = formattedDate + " " + formattedTime;

        return new Button(dateTime, new Icon(VaadinIcon.CALENDAR_CLOCK));
    }
}
