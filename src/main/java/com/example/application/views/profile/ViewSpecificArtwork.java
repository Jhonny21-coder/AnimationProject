package com.example.application.views.profile;

import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.data.Artwork;
import com.example.application.services.ArtworkService;
import com.example.application.data.LikeReaction;
import com.example.application.services.LikeReactionService;
import com.example.application.data.HeartReaction;
import com.example.application.services.HeartReactionService;
import com.example.application.data.Comment;
import com.example.application.services.CommentService;
import com.example.application.views.comment.CommentSectionView;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vaadin.flow.server.VaadinSession;

@Route("view-specific-artwork")
public class ViewSpecificArtwork extends AppLayout implements HasUrlParameter<Long> {

    private final ArtworkService artworkService;
    private final LikeReactionService likeService;
    private final HeartReactionService heartService;
    private final CommentService commentService;
    private final UserServices userService;

    public ViewSpecificArtwork(ArtworkService artworkService,LikeReactionService likeService,
        HeartReactionService heartService, CommentService commentService, UserServices userService){

    	this.artworkService = artworkService;
	this.likeService = likeService;
        this.heartService = heartService;
        this.commentService = commentService;
        this.userService = userService;

    	addClassName("profile-app-layout");
    }

    @Override
    public void setParameter(BeforeEvent event, Long artworkId){
        Artwork artwork = artworkService.getArtworkById(artworkId);
	User user = artwork.getUser();

        displayArtwork(artwork);
        createHeader(user);
        createFooter(user, artwork);
    }

    public void displayArtwork(Artwork artwork){
    	FormLayout formLayout = new FormLayout();
	formLayout.addClassName("specific-form-layout");

    	Image image = new Image();
	image.addClassName("specific-image");

    	String imageFile = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

	try (FileInputStream fis = new FileInputStream(imageFile)) {
             byte[] bytes = fis.readAllBytes();

             StreamResource resource = new StreamResource(artwork.getArtworkUrl(), () -> new ByteArrayInputStream(bytes));

             image.setSrc(resource);
	} catch (Exception e) {
             e.printStackTrace();
	}

	formLayout.add(image);

	setContent(formLayout);
    }

    private void createHeader(User user){
        Icon backButton = new Icon(VaadinIcon.ARROW_BACKWARD);
        backButton.addClassName("profile-back-button");
        backButton.addClickListener(event -> {
             UI.getCurrent().navigate(ArtworkImages.class, user.getId());
        });

        Span fullName = new Span(user.getFullName());
        fullName.addClassName("view-fullname");

        addToNavbar(new HorizontalLayout(backButton, fullName));
    }

    private void createFooter(User user, Artwork artwork){
    	List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());
        List<HeartReaction> totalHeartReactions = heartService.getReactionForArtworkId(artwork.getId());

        User existingUser = artwork.getUser();
        User currentUser = userService.findCurrentUser();

        Button likeButton = createLikeButtonListener(totalLikeReactions, currentUser, artwork);
        Button heartButton = createHeartButtonListener(totalHeartReactions, currentUser, artwork);
        Button commentButton = createCommentButtonListener(existingUser, artwork);

        HorizontalLayout footerLayout = new HorizontalLayout(likeButton, heartButton, commentButton);
        footerLayout.addClassName("specific-footer");

        addToNavbar(true, footerLayout);
    }

    private Button createLikeButtonListener(List<LikeReaction> totalLikeReactions, User user, Artwork artwork){
        Button likeButton = new Button();
        likeButton.addClassName("feed-button");
        likeButton.addClassName("feed-like");

        likeButton.setText(formatValue((long) totalLikeReactions.size() + 999995));

        AtomicBoolean userAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

        if(userAlreadyLiked.get()){
           likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
        }else{
           likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
        }

        AtomicLong atomicLong = new AtomicLong((long) totalLikeReactions.size());

        likeButton.addClickListener(event -> {
           if(userAlreadyLiked.get()){
                likeService.removeLikeReaction(user.getEmail(), artwork.getId());

                atomicLong.decrementAndGet();
                likeButton.setText(formatValue(atomicLong.get() + (long) 999995));
                likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));

                userAlreadyLiked.set(false);
            }else{
                likeService.saveLikeReaction(user.getEmail(), artwork.getId(), (long) 1);

                atomicLong.incrementAndGet();
                likeButton.setText(formatValue(atomicLong.get() + (long) 999995));
                likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));

                userAlreadyLiked.set(true);
            }
        });

        return likeButton;
    }

    private Button createHeartButtonListener(List<HeartReaction> totalHeartReactions, User user, Artwork artwork){
        Button heartButton = new Button();
        heartButton.addClassName("feed-heart");

        heartButton.setText(formatValue((long) totalHeartReactions.size() + 9099995));

        AtomicBoolean userAlreadyHearted = new AtomicBoolean(totalHeartReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

        if(userAlreadyHearted.get()){
           heartButton.setIcon(new Icon(VaadinIcon.HEART));
        }else{
           heartButton.setIcon(new Icon(VaadinIcon.HEART_O));
        }

        AtomicLong atomicLong = new AtomicLong((long) totalHeartReactions.size());

        heartButton.addClickListener(event -> {
             if(userAlreadyHearted.get()){
                heartService.removeHeartReaction(user.getEmail(), artwork.getId());

                atomicLong.decrementAndGet();
                heartButton.setText(formatValue(atomicLong.get() + 9099995));
                heartButton.setIcon(new Icon(VaadinIcon.HEART_O));

                userAlreadyHearted.set(false);
             }else{
                heartService.saveHeartReaction(user.getEmail(), artwork.getId(), (long) 1);

                atomicLong.incrementAndGet();
                heartButton.setText(formatValue(atomicLong.get() + 9099995));
                heartButton.setIcon(new Icon(VaadinIcon.HEART));

                userAlreadyHearted.set(true);
             }
         });

         return heartButton;
    }

    private Button createCommentButtonListener(User user, Artwork artwork){
        List<Comment> comments = commentService.getCommentsByArtworkId(artwork.getId());

	Long totalComments = (long) comments.size();

        Button commentButton = new Button();
        commentButton.addClassName("feed-comment");
        commentButton.setIcon(new Icon(VaadinIcon.COMMENT_ELLIPSIS_O));
        commentButton.setText(formatValue(totalComments));
        commentButton.addClickListener(event -> {
            Long artworkId = artwork.getId();
            VaadinSession.getCurrent().getSession().setAttribute("specific", artworkId);
            UI.getCurrent().navigate(CommentSectionView.class, artwork.getId());
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
}
