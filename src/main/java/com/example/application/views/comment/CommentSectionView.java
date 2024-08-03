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
import com.example.application.data.CommentReaction;
import com.example.application.services.CommentReactionService;
import com.example.application.data.Reply;
import com.example.application.services.ReplyService;
import com.example.application.views.profile.UserProfile;
import com.example.application.views.profile.ViewSpecificArtwork;
import com.example.application.views.profile.ProfileFeed;
import com.example.application.views.MainFeed;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;

import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.time.Instant;

import java.text.DecimalFormat;

@Route("comments")
public class CommentSectionView extends AppLayout implements HasUrlParameter<Long> {

    private final CommentService commentService;
    private final UserServices userService;
    private final ArtworkService artworkService;
    private final LikeReactionService likeService;
    private final HeartReactionService heartService;
    private final CommentReactionService commentReactionService;
    private final ReplyService replyService;

    private final TextArea inputField = new TextArea();
    private Span noComments = new Span("No comments yet");
    private final Grid<User> userGrid = new Grid<>(User.class, false);

    public CommentSectionView(CommentService commentService, UserServices userService, ArtworkService artworkService,
    	LikeReactionService likeService, HeartReactionService heartService, CommentReactionService commentReactionService,
    	ReplyService replyService) {

    	this.commentService = commentService;
        this.userService = userService;
        this.artworkService = artworkService;
	this.likeService = likeService;
        this.heartService = heartService;
        this.commentReactionService = commentReactionService;
	this.replyService = replyService;

        addClassName("profile-app-layout");
    }

    @Override
    public void setParameter(BeforeEvent event, Long artworkId) {
        List<Comment> comments = commentService.getCommentsByArtworkId(artworkId);

        Artwork artwork = artworkService.getArtworkById(artworkId);

        User user = artwork.getUser();

        displayComments(artwork, user);
        createHeader(user, artwork);
    }

    private void displayComments(Artwork artwork, User user){
    	List<LikeReaction> totalLikeReactions = likeService.getReactionForArtworkId(artwork.getId());
        List<HeartReaction> totalHeartReactions = heartService.getReactionForArtworkId(artwork.getId());

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

	Span totalReactions = new Span();
	totalReactions.addClassName("reacted");

	Span commented = new Span();
	commented.addClassName("commented");

        ProfileFeed feed = new ProfileFeed(artworkService, likeService, heartService, commentService, userService);

	Button likeButton = feed.createLikeButtonListener(totalLikeReactions, user, artwork, totalReactions);
	Button heartButton = feed.createHeartButtonListener(totalHeartReactions, user, artwork, totalReactions);
	Button commentButton = feed.createCommentButtonListener(user, artwork);
	commentButton.setEnabled(false);

	HorizontalLayout totalReactionsDiv = feed.createTotalReactions(totalLikeReactions, totalHeartReactions, totalReactions, commented, artwork);

	HorizontalLayout buttonLayout = new HorizontalLayout(likeButton, heartButton, commentButton);
	buttonLayout.addClassName("comment-button-layout");

	VerticalLayout commentLayout = createComment(artwork.getId());
	createFooter(artwork, formLayout, commentButton, commented, commentLayout);

	VerticalLayout userHeader = createUserHeader(user, artwork);
	userHeader.addClassName("comment-user-header-layout");

        formLayout.add(userHeader, image, totalReactionsDiv, buttonLayout, commentLayout);

        setContent(formLayout);
    }

    private VerticalLayout createUserHeader(User user, Artwork artwork){
    	Avatar avatar = new Avatar();
    	avatar.addClassName("comment-user-avatar");

    	Div avatarDiv = new Div(avatar);
	avatarDiv.addClickListener(event -> {
            UI.getCurrent().navigate(UserProfile.class, user.getId());
        });

	Span name = new Span(user.getFullName());
	name.addClassName("comment-user-fullname");
	name.addClickListener(event -> {
            UI.getCurrent().navigate(UserProfile.class, user.getId());
        });

        String avatarFile = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

	try (FileInputStream fis = new FileInputStream(avatarFile)) {
             byte[] bytes = fis.readAllBytes();

             StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

             avatar.setImageResource(resource);
	} catch (Exception e) {
             e.printStackTrace();
	}

	Icon moreIcon = new Icon(VaadinIcon.ELLIPSIS_DOTS_H);
	moreIcon.addClassName("comment-more-icon");
	moreIcon.addClickListener(event -> {
	    Dialog dialog = new Dialog();
	    dialog.addClassName("post-more-dialog");

	    Button button1 = new Button("Save Post", new Icon(VaadinIcon.PLUS_CIRCLE));
	    Button button2 = new Button("Report Post", new Icon(VaadinIcon.EXCLAMATION_CIRCLE));
	    Button button3 = new Button("Hide Post", new Icon(VaadinIcon.CLOSE_CIRCLE));
	    Button button4 = new Button("Add To Favorites", new Icon(VaadinIcon.PIN_POST));
	    Button copyButton = new Button("Copy Link", new Icon(VaadinIcon.LINK));

	    String imageUrl = "http://localhost:8080/shared-artwork/" + artwork.getId();
	    createCopyListener(imageUrl, copyButton);

	    button1.addClassName("post-more-button");
	    button2.addClassName("post-more-button");
	    button3.addClassName("post-more-button");
	    button4.addClassName("post-more-button");
	    copyButton.addClassName("post-more-button");

	    VerticalLayout layout = new VerticalLayout();
	    layout.add(button1, button2, button3, button4, copyButton);

	    dialog.add(layout);

	    dialog.open();
	});

	String description = artwork.getDescription();

	if (description.length() > 37) {
	   description = description.replaceAll("(.{37})", "$1\n");
	}

	Span title = new Span(description);
	title.addClassName("comment-title");

	HorizontalLayout layout = new HorizontalLayout(moreIcon, avatarDiv, name);
	layout.addClassName("comment-user-layout");

	return new VerticalLayout(layout, title);
    }

    private void createCopyListener(String imageUrl, Button copyButton){
        copyButton.addClickListener(event -> {
            copyToClipboard(imageUrl);
            Notification.show("Link copied to clipboard", 1000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
    }

    private void copyToClipboard(String text) {
        String jsCode = "navigator.clipboard.writeText('" + text + "').then(function() { "
                + "console.log('Copying to clipboard was successful!'); "
                + "}, function(err) { "
                + "console.error('Could not copy text: ', err); "
                + "});";
        UI.getCurrent().getPage().executeJs(jsCode);
    }

    private VerticalLayout createComment(Long artworkId){
    	List<Comment> comments = commentService.getCommentsByArtworkId(artworkId);
	Collections.reverse(comments);

	VerticalLayout commentLayout = new VerticalLayout();

	if(comments.isEmpty()){
	    noComments.addClassName("no-comments");

	    commentLayout.add(noComments);
	}

	for(Comment comment : comments){
	    User user = comment.getUser();

	    Avatar avatar = new Avatar();
	    avatar.addClassName("comment-avatar");

	    Div avatarDiv = new Div(avatar);
            avatarDiv.addClassName("comment-avatar-div");
            avatarDiv.addClickListener(event -> {
            	UI.getCurrent().navigate(UserProfile.class, user.getId());
            });

	    Span name = new Span(comment.getFullName());
	    name.addClassName("comment-fullname");
	    name.addClickListener(event -> {
                UI.getCurrent().navigate(UserProfile.class, user.getId());
            });

            String userComments = comment.getComments();

            if (userComments.length() > 28) {
                 userComments = userComments.replaceAll("(.{28})", "$1\n");
             }

	    Span userComment = new Span(userComments);
	    userComment.addClassName("comment-comments");

    	    String avatarFile = "src/main/resources/META-INF/resources/register_images/" + comment.getUserImage();

            try (FileInputStream fis = new FileInputStream(avatarFile)) {
            	byte[] bytes = fis.readAllBytes();

             	StreamResource resource = new StreamResource(comment.getUserImage(), () -> new ByteArrayInputStream(bytes));

             	avatar.setImageResource(resource);
            } catch (Exception e) {
             	e.printStackTrace();
            }

            VerticalLayout layout1 = new VerticalLayout(name, userComment);
	    layout1.addClassName("comment-layout1");

            HorizontalLayout layout2 = new HorizontalLayout(avatarDiv, layout1);

	    Div div = createCommentFooter(comment);
	    div.addClassName("comment-div");

            commentLayout.add(layout2, div);
        }

        return commentLayout;
    }

    private Div createCommentFooter(Comment comment){
    	Span likeButton = new Span("React");
	likeButton.addClassName("comment-buttons");

	Span reacts = new Span();
        reacts.addClassName("reacts");

	showReactions(likeButton, reacts, comment);

	Span replyButton = new Span("Reply");
	replyButton.addClassName("comment-buttons");
	replyButton.addClickListener(event -> {
	    UI.getCurrent().navigate(ReplyCommentView.class, comment.getId());
	});

	Span moreButton = new Span("More");
	moreButton.addClassName("comment-buttons");

	Icon like = new Icon(VaadinIcon.THUMBS_UP);
	like.addClassName("comment-like");

	Icon heart = new Icon(VaadinIcon.HEART);
        heart.addClassName("comment-heart");

	Icon happy = new Icon(VaadinIcon.SMILEY_O);
        happy.addClassName("comment-happy");

        Div reactsDiv = new Div(like, heart, happy, reacts);
        reactsDiv.addClassName("comment-reacts-div");

	List<Reply> replies = replyService.getRepliesByCommentId(comment.getId());

	Span viewReply = new Span();

	if(!replies.isEmpty()){
	    viewReply.setVisible(true);

	    if(replies.size() == 1){
	       viewReply.setText("View " + formatValue(replies.size()) + " reply");
	       viewReply.addClassName("comment-view-reply");
	    }else{
	       viewReply.setText("View " + formatValue(replies.size()) + " replies");
	       viewReply.addClassName("comment-view-reply");
	    }
	}else{
	    viewReply.setVisible(false);
	}

	viewReply.addClickListener(event -> {
            UI.getCurrent().navigate(ReplyCommentView.class, comment.getId());
        });

	return new Div(likeButton, replyButton, moreButton, reactsDiv, viewReply);
    }

    /*private void menuIconListener(){
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setCancelable(true);
        dialog.setConfirmText("Yes");
        dialog.setHeader("Delete comment?");
        dialog.setText("Are you sure you want to delete this comment?");
        dialog.addConfirmListener(event -> {
            User reactor = userService.findCurrentUser();

            commentService.deleteComment(reactor.getId(), comment.getId());
        });

        dialog.addClickListener(event -> {
            dialog.open();
        });
    }*/

    private void showReactions(Span likeButton, Span reacts, Comment comment){
    	List<CommentReaction> reactions = commentReactionService.getCommentReactionsByCommentId(comment.getId());

     	Dialog dialog = new Dialog();
        dialog.addClassName("comment-dialog");

	AtomicLong totalReacts = new AtomicLong(reactions.size());

	if(totalReacts.get() != 0){
           reacts.setText(formatValue(totalReacts.get()));
        }

	User currentUser = userService.findCurrentUser();

	CommentReaction reactor = commentReactionService.getCommentReactionByReactorAndCommentId(currentUser.getId(), comment.getId());

	AtomicBoolean isReacted = new AtomicBoolean(reactor != null);

	if(isReacted.get() && reactor.getReactType().equalsIgnoreCase("Like")){
	    likeButton.setText("Reacted");
            likeButton.getStyle().set("color", "var(--lumo-primary-color)");
	}else if(isReacted.get() && reactor.getReactType().equalsIgnoreCase("Heart")){
	    likeButton.setText("Reacted");
            likeButton.getStyle().set("color", "var(--lumo-error-color)");
	}else if(isReacted.get() && reactor.getReactType().equalsIgnoreCase("Happy")){
	    likeButton.setText("Reacted");
            likeButton.getStyle().set("color", "var(--lumo-warning-color)");
	}

	Icon likeIcon = new Icon(VaadinIcon.THUMBS_UP);
        likeIcon.addClassName("like-react-icon");
        likeIcon.addClickListener(e -> {
            createButtonsListener(isReacted, "Like", totalReacts, likeButton, reacts, comment, "primary");
            dialog.close();
        });

	Icon heartIcon = new Icon(VaadinIcon.HEART);
	heartIcon.addClassName("heart-react-icon");
	heartIcon.addClickListener(e -> {
            createButtonsListener(isReacted, "Heart", totalReacts, likeButton, reacts, comment, "error");
            dialog.close();
        });

	Icon happyIcon = new Icon(VaadinIcon.SMILEY_O);
	happyIcon.addClassName("happy-react-icon");
	happyIcon.addClickListener(e -> {
            createButtonsListener(isReacted, "Happy", totalReacts, likeButton, reacts, comment, "warning");
            dialog.close();
        });

	dialog.add(
	    new VerticalLayout(likeIcon, new Span("Like")),
	    new VerticalLayout(heartIcon, new Span("Heart")),
	    new VerticalLayout(happyIcon, new Span("Happy"))
	);

    	likeButton.addClickListener(event -> {
             dialog.open();
        });
    }

    public void createButtonsListener(AtomicBoolean isReacted, String reactType, AtomicLong totalReacts, Span button,
        Span reacts, Comment comment, String colorTheme){

	User currentUser = userService.findCurrentUser();

        if(!isReacted.get()){
           commentReactionService.saveCommentReaction(comment, currentUser, reactType);

	   totalReacts.incrementAndGet();

           reacts.setText(String.valueOf(totalReacts.get()));

           button.setText("Reacted");
           button.getStyle().set("color", "var(--lumo-" + colorTheme + "-color)");

           isReacted.set(true);
        }else{
           Long reactorId = currentUser.getId();
           Long commentId = comment.getId();

           CommentReaction reactor = commentReactionService.getCommentReactionByReactorAndCommentId(reactorId, commentId);

           if(reactor.getReactType().equalsIgnoreCase(reactType)){
              commentReactionService.removeCommentReaction(reactorId, commentId);

	      totalReacts.decrementAndGet();

              if(totalReacts.get() == 0){
                 reacts.setText("");
              }else{
                 reacts.setText(String.valueOf(totalReacts.get()));
              }

              button.setText("React");
              button.getStyle().set("color", "var(--lumo-contrast-70pct)");

              isReacted.set(false);
            }else{
              commentReactionService.updateCommentReaction(reactor, reactType);

              button.setText("Reacted");
              button.getStyle().set("color", "var(--lumo-" + colorTheme + "-color)");

              isReacted.set(true);
            }
        }
    }

    private void createFooter(Artwork artwork, FormLayout formLayout, Button commentButton, Span commented, VerticalLayout commentLayout){
	Button sendIcon = new Button();
    	sendIcon.setIcon(new Icon(VaadinIcon.PAPERPLANE));
	sendIcon.addClassName("comment-send-icon");
	sendIcon.setEnabled(false);
	sendIcon.addClickListener(event -> {
	     noComments.setVisible(false);

	     User user = userService.findCurrentUser();

	     VerticalLayout singleComment = createSingleComment(artwork, commentButton, commented);

	     inputField.clear();

	     formLayout.addComponentAtIndex(4, singleComment);
	});

	inputField.addClassName("comment-input-field");
	inputField.setPlaceholder("Write a comment...");
	inputField.setValueChangeMode(ValueChangeMode.EAGER);
	inputField.addValueChangeListener(event -> {
	     String value = event.getValue();

	     if(!value.isEmpty() && !value.matches("\\s*")){
	     	sendIcon.setEnabled(true);
	     	sendIcon.getStyle().set("color", "var(--lumo-primary-color)");

		Dialog dialog = new Dialog();
		dialog.addClassName("all-users-dialog");
		dialog.setHeaderTitle("Mention A Follower");

	     	if(value.equals("@")){
	     	   createUsersDiv(dialog);
	     	}
	     }else{
	    	sendIcon.setEnabled(false);
	    	sendIcon.getStyle().set("color", "var(--lumo-contrast-50pct)");
	     }
	});

	Upload upload = createUploadImage();
    	addToNavbar(true, upload, inputField, sendIcon);
    }

    private void createUsersDiv(Dialog dialog){
    	dialog.open();
    	List<User> users = userService.getAllUsers();

    	userGrid.setItems(users);
    	userGrid.removeAllColumns();
    	userGrid.addClassName("all-users-grid");

	TextField field = new TextField();
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.setPlaceholder("Find more friends...");
        field.addValueChangeListener(event -> updateList(dialog, field.getValue()));
        dialog.add(field);

	userGrid.addComponentColumn(user -> {
            Avatar avatar = new Avatar();
            avatar.addClassName("view-avatar");

            String avatarFile = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

            try (FileInputStream fis = new FileInputStream(avatarFile)) {
                byte[] bytes = fis.readAllBytes();

                StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

                avatar.setImageResource(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Span fullName = new Span(user.getFullName());
            fullName.addClassName("view-firstname");

            HorizontalLayout  avatarDiv = new HorizontalLayout(avatar, fullName);
            avatarDiv.addClassName("view-avatar-div");
            avatarDiv.addClickListener(event -> {
                inputField.setValue("@" + user.getFullName());
                dialog.close();
            });

            return avatarDiv;
        }).setAutoWidth(false);

        dialog.add(userGrid);
    }

    private void updateList(Dialog dialog, String searchTerm) {
	List<User> users = userService.findAllUsers(searchTerm.replace("@", ""));

	userGrid.setItems(users);
    }

    private VerticalLayout createSingleComment(Artwork artwork, Button commentButton, Span commented){
        VerticalLayout commentLayout = new VerticalLayout();

	User user = userService.findCurrentUser();

        Avatar avatar = new Avatar();
        avatar.addClassName("comment-avatar");

	Div avatarDiv = new Div(avatar);
	avatarDiv.addClassName("comment-avatar-div");
	avatarDiv.addClickListener(event -> {
	    UI.getCurrent().navigate(UserProfile.class, user.getId());
	});

	Span name = new Span(user.getFullName());
	name.addClassName("comment-fullname");
	name.addClickListener(event -> {
            UI.getCurrent().navigate(UserProfile.class, user.getId());
	});

	String commentValue = inputField.getValue();

	if (commentValue.length() > 28) {
            commentValue = commentValue.replaceAll("(.{28})", "$1\n");
	}

	Span userComment = new Span(commentValue);

	userComment.addClassName("comment-comments");

	String avatarFile = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

	try (FileInputStream fis = new FileInputStream(avatarFile)) {
             byte[] bytes = fis.readAllBytes();

             StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

             avatar.setImageResource(resource);
	} catch (Exception e) {
                e.printStackTrace();
	}

	VerticalLayout layout1 = new VerticalLayout(name, userComment);
	layout1.addClassName("comment-layout1");

	HorizontalLayout layout2 = new HorizontalLayout(avatarDiv, layout1);

	// Save comment to database
	saveCommentToDatabase(user, artwork, commented);

	List<Comment> totalComments = commentService.getCommentsByArtworkId(artwork.getId());

	commentButton.setText(String.valueOf(totalComments.size()));

	Long commentId = totalComments.get(totalComments.size() - 1).getId();

	Comment comment = commentService.getCommentById(commentId);

	Div div = createCommentFooter(comment);
	div.addClassName("comment-div");

	commentLayout.add(layout2, div);

        return commentLayout;
    }

    private void saveCommentToDatabase(User user, Artwork artwork, Span commented){
    	String email = user.getEmail();
        String fullName = user.getFullName();
        Instant dateTime = Instant.now();
        String comments = inputField.getValue();
        String userImage = user.getProfileImage();
        Long artworkId = artwork.getId();

        commentService.saveComment(email, fullName, dateTime, comments, userImage, artworkId);

        List<Comment> commentss = commentService.getCommentsByArtworkId(artwork.getId());
        commented.setText(formatValue((long) commentss.size()) + " comments");
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

    private Upload createUploadImage(){
        Icon cameraIcon = new Icon(VaadinIcon.CAMERA);
        cameraIcon.addClassName("comment-camera-icon");

        Upload upload = new Upload(new MemoryBuffer());
        upload.setUploadButton(cameraIcon);
        upload.addClassName("comment-upload");
        upload.setAcceptedFileTypes("image/png");
        upload.addSucceededListener(event -> {
            MemoryBuffer buffer = (MemoryBuffer) upload.getReceiver();

            try {
                InputStream inputStream = buffer.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                String originalFilename = event.getFileName();

                StreamResource resource = new StreamResource(originalFilename, () -> new ByteArrayInputStream(bytes));

                String filePath = "/src/main/resources/META-INF/resources/comments_images/" + originalFilename;
                FileOutputStream outputStream = new FileOutputStream(filePath);
                //outputStream.write(bytes);

            } catch (IOException e) {
                Notification.show("Error uploading artwork image", 3000, Notification.Position.TOP_CENTER);
            }
        });

        return upload;
     }

    private void createHeader(User user, Artwork artwork){
        Span fullName = new Span(user.getFullName());
        fullName.addClassName("comment-first-name");

        Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("comment-back-icon");
        backIcon.addClickListener(event -> {
            Set<String> sessionNames = VaadinSession.getCurrent().getSession().getAttributeNames();

            for(String sessionName : sessionNames){
                if(sessionName.equals("main")){
                   VaadinSession.getCurrent().getSession().removeAttribute("main");
                   UI.getCurrent().navigate(MainFeed.class);

                }else if(sessionName.equals("profile")){
                   VaadinSession.getCurrent().getSession().removeAttribute("profile");
                   UI.getCurrent().navigate(UserProfile.class, user.getId());

                }else if(sessionName.equals("specific")){
                   VaadinSession.getCurrent().getSession().removeAttribute("specific");
                   UI.getCurrent().navigate(ViewSpecificArtwork.class, artwork.getId());
                }
            }
        });

        addToNavbar(backIcon, fullName);
     }
}
