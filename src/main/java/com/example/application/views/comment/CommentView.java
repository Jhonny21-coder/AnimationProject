package com.example.application.views.comment;

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
import com.example.application.views.comment.CommentSectionView;
import com.example.application.views.MainLayout;
import com.example.application.views.EmcView;

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
import com.vaadin.flow.component.Component;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Locale;
import java.util.List;

import java.text.DecimalFormat;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CopyOnWriteArrayList;

import com.vaadin.flow.server.VaadinSession;
import java.util.concurrent.CopyOnWriteArrayList;

@Route(value = "emc-viewww", layout=MainLayout.class)
public class CommentView extends AppLayout {

    private final ArtworkService artworkService;
    private final LikeReactionService likeService;
    private final HeartReactionService heartService;
    private final CommentService commentService;
    private final UserServices userService;
    private static final CopyOnWriteArrayList<UI> uiList = new CopyOnWriteArrayList<>();
    private Button likeButton;
    private Button heartButton;
    private Span totalReactions;
    private static Long likeLong = 0L;

    public CommentView(ArtworkService artworkService, LikeReactionService likeService,
    	HeartReactionService heartService, CommentService commentService, UserServices userService){

        this.artworkService = artworkService;
        this.likeService = likeService;
        this.heartService = heartService;
        this.commentService = commentService;
        this.userService = userService;

        addClassName("main-feed");

        init();
    }

    protected void init() {
        List<Artwork> artworks = artworkService.getAllArtworks();

	FormLayout formLayout = new FormLayout();

        for(Artwork artwork : artworks){
            totalReactions = new Span(formatValue(likeLong) + " reactions");
            UI currentUI = UI.getCurrent();
            uiList.add(currentUI);

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

            User user = artwork.getUser();
	    User currentUser = userService.findCurrentUser();

            List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());
            List<HeartReaction> totalHeartReactions = heartService.getReactionForArtworkId(artwork.getId());

	    AtomicBoolean isrAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(currentUser.getId())));
            AtomicBoolean isAlreadyHearted = new AtomicBoolean(totalHeartReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(currentUser.getId())));
            // Like Button
            likeButton = new Button();
            likeButton.addClassName("feed-like");
            likeButton.setText(formatValue((long) totalLikeReactions.size()));
	    likeButton.addClickListener(event -> {
	    	List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());

	    	likeLong += (long) totalLikes.size();

            	updateButton(currentUI, currentUser, artwork, "Like");
            	broadcastUpdate(currentUser, artwork, "Like", isrAlreadyLiked);
            });

	    // Heart button
	    heartButton = new Button();
	    heartButton.addClassName("feed-heart");
            heartButton.setText(formatValue((long) totalHeartReactions.size()));
	    heartButton.addClickListener(event -> {
            	updateButton(currentUI, currentUser, artwork, "Heart");
            	broadcastUpdate(currentUser, artwork, "Heart", isAlreadyHearted);
            });

            boolean userAlreadyLiked = totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(currentUser.getId()));
            boolean userAlreadyHearted = totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(currentUser.getId()));

            if(userAlreadyLiked){
            	likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
            }else{
            	likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
            }

            if(userAlreadyHearted){
           	heartButton.setIcon(new Icon(VaadinIcon.HEART));
            }else{
           	heartButton.setIcon(new Icon(VaadinIcon.HEART_O));
            }

            Span commented = new Span();
            commented.addClassName("commented");

            Button commentButton = createCommentButtonListener(user, artwork);

            Icon shareButton = new Icon(VaadinIcon.LINK);
            shareButton.addClassName("feed-share");

            HorizontalLayout buttonsLayout = new HorizontalLayout(likeButton, heartButton, commentButton, shareButton);
            buttonsLayout.addClassName("main-feed-buttons");

            HorizontalLayout profileLayout = createFeedHeader(user, artwork.getId());
            profileLayout.addClassName("profile-layout2");

            Button datePosted = createDateTimePosted(artwork);
            datePosted.addClassName("feed-date-posted");

            HorizontalLayout totalReactionsDiv = createTotalReactions(totalLikeReactions, totalHeartReactions, commented, artwork);
            totalReactionsDiv.addClassName("comment-reactions-div");

            formLayout.add(profileLayout, datePosted, image, totalReactionsDiv, buttonsLayout);

            currentUI.addDetachListener(event -> uiList.remove(currentUI));
        }

        setContent(formLayout);
    }

    private HorizontalLayout createTotalReactions(List<LikeReaction> totalLikeReactions, List<HeartReaction> totalHeartReactions, Span commented, Artwork artwork){

        Icon likeIcon = new Icon(VaadinIcon.THUMBS_UP);
        likeIcon.addClassName("reactions-like");

        Icon heartIcon = new Icon(VaadinIcon.HEART);
        heartIcon.addClassName("reactions-heart");

        Icon happyIcon = new Icon(VaadinIcon.SMILEY_O);
        happyIcon.addClassName("reactions-happy");

        Long totals = (long) totalLikeReactions.size() + totalHeartReactions.size();

	//totalReactions = new Span(formatValue(totals) + " reactions");
	totalReactions.addClassName("reacted");

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

        String avatarFile = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

        try (FileInputStream fis = new FileInputStream(avatarFile)) {
             byte[] bytes = fis.readAllBytes();

             StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

             avatar.setImageResource(resource);
        } catch (Exception e) {
             e.printStackTrace();
        }

        Div avatarDiv = new Div(avatar);
        avatarDiv.addClassName("feed-avatar-div");
        avatarDiv.addClickListener(event -> {
            User currentUser = userService.findCurrentUser();

            if(!currentUser.getId().equals(user.getId())){
                UI.getCurrent().navigate(UserProfile.class, user.getId());
            }else{
                UI.getCurrent().navigate(EmcView.class);
            }
        });

        return new HorizontalLayout(avatarDiv, name);
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
            VaadinSession.getCurrent().getSession().setAttribute("main", artworkId);

            UI.getCurrent().navigate(CommentSectionView.class, artworkId);
        });

        return commentButton;
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

    private void updateButton(UI ui, User user, Artwork artwork, String reactType){
    	ui.access(() -> {
    	    User currentUser = userService.findCurrentUser();

            List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());

            AtomicBoolean userAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(currentUser.getId())));

	    //AtomicLong likeLong = new AtomicLong((long) totalLikeReactions.size());

            if(userAlreadyLiked.get()){
              likeLong++;

              likeService.removeLikeReaction(user.getEmail(), artwork.getId());

              likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
              likeButton.setText(formatValue(likeLong));

              List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
              List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

              Long totals = (long) totalLikes.size() + totalHearts.size();

              totalReactions.setText(formatValue(likeLong) + " reactions");

              userAlreadyLiked.set(false);
           }else{
              likeLong--;

              likeService.saveLikeReaction(user.getEmail(), artwork.getId(), (long) 1);

              likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
              likeButton.setText(formatValue(likeLong));

              List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
              List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

              Long totals = (long) totalLikes.size() + totalHearts.size();

              totalReactions.setText(formatValue(likeLong) + " reactions");

              userAlreadyLiked.set(true);
           }
        });
    }

    public void handleClickListener( User user, Artwork artwork, String reactType){
	List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());
	List<HeartReaction> totalHeartReactions = heartService.getReactionForArtworkId(artwork.getId());

        AtomicBoolean userAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));
        AtomicBoolean userAlreadyHearted = new AtomicBoolean(totalHeartReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

	AtomicLong likeLong = new AtomicLong((long) totalLikeReactions.size());
	AtomicLong heartLong = new AtomicLong((long) totalHeartReactions.size());

        DecimalFormat formatter = new DecimalFormat("#,##");

        if(userAlreadyLiked.get() || userAlreadyHearted.get()){
           if(reactType.equals("Like")){
              likeLong.decrementAndGet();

              likeService.removeLikeReaction(user.getEmail(), artwork.getId());

              likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
              likeButton.setText(formatValue(likeLong.get()));
           }else if(reactType.equals("Heart")){
              heartLong.decrementAndGet();

              heartService.removeHeartReaction(user.getEmail(), artwork.getId());

              heartButton.setIcon(new Icon(VaadinIcon.HEART_O));
              heartButton.setText(formatValue(heartLong.get()));
	   }

           List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
           List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

           Long totals = (long) totalLikes.size() + totalHearts.size();

	   totalReactions.setText(formatValue(totals) + " reactions");
        }else{
           if(reactType.equals("Like")){
              likeLong.incrementAndGet();

              likeService.saveLikeReaction(user.getEmail(), artwork.getId(), (long) 1);

              likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
              likeButton.setText(formatValue(likeLong.get()));
	   }else if(reactType.equals("Heart")){
	      heartLong.incrementAndGet();

              heartService.saveHeartReaction(user.getEmail(), artwork.getId(), (long) 1);

              heartButton.setIcon(new Icon(VaadinIcon.HEART));
              heartButton.setText(formatValue(heartLong.get()));
           }

	   List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
           List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

           Long totals = (long) totalLikes.size() + totalHearts.size();

           totalReactions.setText(formatValue(totals) + " reactions");
        }
    }

    private void broadcastUpdate(User user, Artwork artwork, String reactType, AtomicBoolean isAlreadyReacted) {
        for (UI ui : uiList) {
            ui.access(() -> {
                // Ensure UI is still attached before updating
                if (ui.isAttached()) {
                    for (CommentView view : ui.getChildren()
                                            .filter(CommentView.class::isInstance)
                                            .map(CommentView.class::cast)
                                            .toList()) {

                        /*List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());
        		List<HeartReaction> totalHeartReactions = heartService.getReactionForArtworkId(artwork.getId());

        		AtomicBoolean userAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));
        		AtomicBoolean userAlreadyHearted = new AtomicBoolean(totalHeartReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(user.getId())));

        		AtomicLong likeLong = new AtomicLong((long) totalLikeReactions.size());
        		AtomicLong heartLong = new AtomicLong((long) totalHeartReactions.size());

        		DecimalFormat formatter = new DecimalFormat("#,##");

        		if(isAlreadyReacted.get()){
           		   if(reactType.equals("Like")){
              	  	      likeLong.decrementAndGet();

              		      likeService.removeLikeReaction(user.getEmail(), artwork.getId());

              		      view.likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
              		      view.likeButton.setText(formatValue(likeLong.get()));
           		   }else if(reactType.equals("Heart")){
              		      heartLong.decrementAndGet();

              		      heartService.removeHeartReaction(user.getEmail(), artwork.getId());

              		      view.heartButton.setIcon(new Icon(VaadinIcon.HEART_O));
              		      view.heartButton.setText(formatValue(heartLong.get()));
           		   }

           	    	   List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
           	    	   List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

           	    	   Long totals = (long) totalLikes.size() + totalHearts.size();

           		   view.totalReactions.setText(formatValue(totals) + " reactions");
        	    	}else{
           		   if(reactType.equals("Like")){
              		     likeLong.incrementAndGet();

              		     likeService.saveLikeReaction(user.getEmail(), artwork.getId(), (long) 1);

              		     view.likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
              		     view.likeButton.setText(formatValue(likeLong.get()));
           		   }else if(reactType.equals("Heart")){
              		     heartLong.incrementAndGet();

              	 	     heartService.saveHeartReaction(user.getEmail(), artwork.getId(), (long) 1);

              		     view.heartButton.setIcon(new Icon(VaadinIcon.HEART));
              		     view.heartButton.setText(formatValue(heartLong.get()));
           		  }

          		  List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
           		  List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

           		  Long totals = (long) totalLikes.size() + totalHearts.size();

           		  view.totalReactions.setText(formatValue(totals) + " reactions");
                        }*/

                        User currentUser = userService.findCurrentUser();

            		List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());

            		AtomicBoolean userAlreadyLiked = new AtomicBoolean(totalLikeReactions.stream().anyMatch(reaction -> reaction.getUser().getId().equals(currentUser.getId())));

            		//AtomicLong likeLong = new AtomicLong((long) totalLikeReactions.size());

            		if(userAlreadyLiked.get()){
              		   likeLong++;

              	 	   likeService.removeLikeReaction(user.getEmail(), artwork.getId());

              		   view.likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP_O));
              		   view.likeButton.setText(formatValue(likeLong));

              		   List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
              		   List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

              		   Long totals = (long) totalLikes.size() + totalHearts.size();

              		   view.totalReactions.setText(formatValue(likeLong) + " reactions");

              		   userAlreadyLiked.set(false);
           		}else{
              		   likeLong--;

              		   likeService.saveLikeReaction(user.getEmail(), artwork.getId(), (long) 1);

              		   view.likeButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
              		   view.likeButton.setText(formatValue(likeLong));

              		   List<LikeReaction> totalLikes = likeService.getReactionForArtworkId(artwork.getId());
              		   List<HeartReaction> totalHearts = heartService.getReactionForArtworkId(artwork.getId());

              		   Long totals = (long) totalLikes.size() + totalHearts.size();

              		   view.totalReactions.setText(formatValue(likeLong) + " reactions");

              		   userAlreadyLiked.set(true);
           		}
                    }
               }
            });
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
