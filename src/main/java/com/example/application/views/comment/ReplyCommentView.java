package com.example.application.views.comment;

import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.services.ReplyService;
import com.example.application.data.Comment;
import com.example.application.services.CommentService;
import com.example.application.data.Reply;
import com.example.application.services.ReplyService;
import com.example.application.data.CommentReaction;
import com.example.application.services.CommentReactionService;
import com.example.application.data.ReplyReaction;
import com.example.application.services.ReplyReactionService;
import com.example.application.views.profile.UserProfile;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;

@Route("reply-comment")
public class ReplyCommentView extends AppLayout implements HasUrlParameter<Long> {

    private final CommentService commentService;
    private final CommentReactionService commentReactionService;
    private final ReplyReactionService replyReactionService;
    private final ReplyService replyService;
    private final UserServices userService;
    private final TextArea inputField = new TextArea();
    private final Span noReply = new Span("No reply yet");

    public ReplyCommentView(CommentService commentService, ReplyService replyService,
    	UserServices userService, CommentReactionService commentReactionService,
    	ReplyReactionService replyReactionService){

    	this.commentService = commentService;
    	this.replyService = replyService;
    	this.userService = userService;
    	this.commentReactionService = commentReactionService;
    	this.replyReactionService = replyReactionService;

    	addClassName("profile-app-layout");
    }

    @Override
    public void setParameter(BeforeEvent event, Long commentId) {
        Comment comment = commentService.getCommentById(commentId);

	inputField.setValue("@" + comment.getFullName());

        List<Reply> replies = replyService.getRepliesByCommentId(commentId);

        createReplyComment(comment, replies);
        createHeader(comment);
    }

    private void createReplyComment(Comment comment, List<Reply> replies){
    	FormLayout formLayout = new FormLayout();

	Span replied = new Span();
	replied.addClassName("total-replies");

	if(!replies.isEmpty()){
	   if(replies.size() == 1){
	      replied.setText(String.valueOf(replies.size()) + " reply");
	   }else{
	      replied.setText(String.valueOf(replies.size()) + " replies");
	   }
	}

	createFooter(comment, formLayout, replied);
    	User user = comment.getUser();

	Avatar avatar = new Avatar();
	avatar.addClassName("reply-user-avatar");

	Div avatarDiv = new Div(avatar);
	avatarDiv.addClassName("reply-avatar-div");
	avatarDiv.addClickListener(event -> {
            UI.getCurrent().navigate(UserProfile.class, user.getId());
	});

	Span name = new Span(comment.getFullName());
	name.addClassName("reply-fullname");
	name.addClickListener(event -> {
            UI.getCurrent().navigate(UserProfile.class, user.getId());
	});

	String commentValue = comment.getComments();

	if (commentValue.length() > 43) {
            commentValue = commentValue.replaceAll("(.{43})", "$1\n");
	}

	Span userComment = new Span(commentValue);
	userComment.addClassName("reply-comments");

	String avatarFile = "src/main/resources/META-INF/resources/register_images/" + comment.getUserImage();

	try (FileInputStream fis = new FileInputStream(avatarFile)) {
            byte[] bytes = fis.readAllBytes();

	    StreamResource resource = new StreamResource(comment.getUserImage(), () -> new ByteArrayInputStream(bytes));

            avatar.setImageResource(resource);
	} catch (Exception e) {
            e.printStackTrace();
	}

	VerticalLayout parentLayout = new VerticalLayout(avatarDiv, name, userComment);
        parentLayout.addClassName("reply-parent-layout");

	FormLayout replyFooter = createCommentFooter(comment, replied);

	VerticalLayout replyLayout = createReplyLayout(replies);

	formLayout.add(parentLayout, replyFooter, replyLayout);

	setContent(formLayout);
    }

    private VerticalLayout createReplyLayout(List<Reply> replies){
        VerticalLayout commentLayout = new VerticalLayout();

        if(replies.isEmpty()){
            noReply.addClassName("no-comments");

            commentLayout.add(noReply);
        }

        for(Reply reply : replies){
            User user = reply.getReplier();

            Avatar avatar = new Avatar();
            avatar.addClassName("comment-avatar");

            Div avatarDiv = new Div(avatar);
            avatarDiv.addClassName("comment-avatar-div");
            avatarDiv.addClickListener(event -> {
                UI.getCurrent().navigate(UserProfile.class, user.getId());
            });

            Span name = new Span(reply.getReplier().getFullName());
            name.addClassName("comment-fullname");
            name.addClickListener(event -> {
                UI.getCurrent().navigate(UserProfile.class, user.getId());
            });

            String userReply = reply.getReply();

            if (userReply.length() > 28) {
                 userReply = userReply.replaceAll("(.{28})", "$1\n");
             }

            Span userComment = new Span(userReply);
            userComment.addClassName("comment-comments");

            String avatarFile = "src/main/resources/META-INF/resources/register_images/" + reply.getReplier().getProfileImage();

            try (FileInputStream fis = new FileInputStream(avatarFile)) {
                byte[] bytes = fis.readAllBytes();

                StreamResource resource = new StreamResource(reply.getReplier().getProfileImage(), () -> new ByteArrayInputStream(bytes));

                avatar.setImageResource(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }

            VerticalLayout layout1 = new VerticalLayout(name, userComment);
            layout1.addClassName("comment-layout1");

            HorizontalLayout layout2 = new HorizontalLayout(avatarDiv, layout1);

	    Comment comment = reply.getComment();

            Div div = createFooterReply(reply);
            div.addClassName("comment-div");

            commentLayout.add(layout2, div);
        }

        return commentLayout;
    }

    private Div createFooterReply(Reply reply){
        Span reactButton = new Span("React");
        reactButton.addClassName("comment-buttons");

        Span reacts = new Span();
        reacts.addClassName("reacts");

        showReplyReactions(reactButton, reacts, reply);

        Span replyButton = new Span("Reply");
        replyButton.addClassName("comment-buttons");
        replyButton.addClickListener(event -> {
            inputField.setValue("@" + reply.getReplier().getFullName());
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

        return new Div(reactButton, replyButton, moreButton, reactsDiv);
    }

    private void showReplyReactions(Span likeButton, Span reacts, Reply reply){
        List<ReplyReaction> reactions = replyReactionService.getReplyReactionsByReplyId(reply.getId());

        Dialog dialog = new Dialog();
        dialog.addClassName("comment-dialog");

        AtomicLong totalReacts = new AtomicLong(reactions.size());

        if(totalReacts.get() != 0){
           reacts.setText(formatValue(totalReacts.get()));
        }

        User currentUser = userService.findCurrentUser();

        ReplyReaction reactor = replyReactionService.getReplyReactionByReactorAndReplyId(currentUser.getId(), reply.getId());

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
            createReplyButtonsListener(isReacted, "Like", totalReacts, likeButton, reacts, reply, "primary");
            dialog.close();
        });

        Icon heartIcon = new Icon(VaadinIcon.HEART);
        heartIcon.addClassName("heart-react-icon");
        heartIcon.addClickListener(e -> {
            createReplyButtonsListener(isReacted, "Heart", totalReacts, likeButton, reacts, reply, "error");
            dialog.close();
        });

        Icon happyIcon = new Icon(VaadinIcon.SMILEY_O);
        happyIcon.addClassName("happy-react-icon");
        happyIcon.addClickListener(e -> {
            createReplyButtonsListener(isReacted, "Happy", totalReacts, likeButton, reacts, reply, "warning");
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

    private void createReplyButtonsListener(AtomicBoolean isReacted, String reactType, AtomicLong totalReacts, Span button,
        Span reacts, Reply reply, String colorTheme){

        User currentUser = userService.findCurrentUser();

        if(!isReacted.get()){
           replyReactionService.saveReplyReaction(reply, currentUser, reactType);

           totalReacts.incrementAndGet();

           reacts.setText(String.valueOf(totalReacts.get()));

           button.setText("Reacted");
           button.getStyle().set("color", "var(--lumo-" + colorTheme + "-color)");

           isReacted.set(true);
        }else{
           Long reactorId = currentUser.getId();
           Long replyId = reply.getId();

           ReplyReaction reactor = replyReactionService.getReplyReactionByReactorAndReplyId(reactorId, replyId);

           if(reactor.getReactType().equalsIgnoreCase(reactType)){
              replyReactionService.removeReplyReaction(reactorId, replyId);

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
              replyReactionService.updateReplyReaction(reactor, reactType);

              button.setText("Reacted");
              button.getStyle().set("color", "var(--lumo-" + colorTheme + "-color)");

              isReacted.set(true);
            }
        }
    }

    // For Footer
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

        //dialog.add(likeIcon, heartIcon, happyIcon);
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
              button.getStyle().set("color", "white");

              isReacted.set(false);
            }else{
              commentReactionService.updateCommentReaction(reactor, reactType);

              button.setText("Reacted");
              button.getStyle().set("color", "var(--lumo-" + colorTheme + "-color)");

              isReacted.set(true);
            }
        }
    }

    private FormLayout createCommentFooter(Comment comment, Span replied){
        Span likeButton = new Span("React");
        likeButton.addClassName("reply-buttons");

        Span reacts = new Span();
        reacts.addClassName("reply-reacts");

        showReactions(likeButton, reacts, comment);

        Span replyButton = new Span("Reply");
        replyButton.addClassName("reply-buttons");
	replyButton.addClickListener(event -> {
	    inputField.setValue("@" + comment.getFullName());
	});

        Span moreButton = new Span("More");
        moreButton.addClassName("reply-buttons");

	Span viewPost = new Span("View Post");
	viewPost.addClassName("reply-buttons");
	viewPost.addClickListener(event -> {
	     Long artworkId = comment.getArtwork().getId();

	     UI.getCurrent().navigate(CommentSectionView.class, artworkId);
	});

        Icon like = new Icon(VaadinIcon.THUMBS_UP);
        like.addClassName("reply-like");

        Icon heart = new Icon(VaadinIcon.HEART);
        heart.addClassName("reply-heart");

        Icon happy = new Icon(VaadinIcon.SMILEY_O);
        happy.addClassName("reply-happy");

        Div reactsDiv = new Div(like, heart, happy, reacts, replied);
        reactsDiv.addClassName("reply-reacts-div");

	HorizontalLayout layout = new HorizontalLayout(likeButton, replyButton, moreButton, viewPost);
	layout.addClassName("reply-buttons-layout");

        return new FormLayout(reactsDiv, layout);
    }

    private void createFooter(Comment comment, FormLayout formLayout, Span replied){
        Button sendIcon = new Button();
        sendIcon.setIcon(new Icon(VaadinIcon.PAPERPLANE));
        sendIcon.addClassName("comment-send-icon");
        sendIcon.setEnabled(false);
	sendIcon.addClickListener(event -> {
	    noReply.setVisible(false);

	    VerticalLayout singleReply = createSingleReply(comment, replied);

            formLayout.add(singleReply);

            inputField.clear();
	});

        inputField.addClassName("comment-input-field");
        inputField.setPlaceholder("Write a comment...");
        inputField.setValueChangeMode(ValueChangeMode.EAGER);
        inputField.addValueChangeListener(event -> {
             String value = event.getValue();

             if(!value.isEmpty() && !value.matches("\\s*")){
                sendIcon.setEnabled(true);
                sendIcon.getStyle().set("color", "var(--lumo-primary-color)");
             }else{
                sendIcon.setEnabled(false);
                sendIcon.getStyle().set("color", "var(--lumo-contrast-50pct)");
             }
        });

        Upload upload = createUploadImage();
        addToNavbar(true, upload, inputField, sendIcon);
    }

    private Div createSingleReplyFooter(Reply reply){
        Span likeButton = new Span("React");
        likeButton.addClassName("comment-buttons");

        Span reacts = new Span();
        reacts.addClassName("reacts");

        showReplyReactions(likeButton, reacts, reply);

	Span replyButton = new Span("Reply");
        replyButton.addClassName("comment-buttons");
        replyButton.addClickListener(event -> {
            inputField.setValue("@" + reply.getReplier().getFullName());
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

        return new Div(likeButton, replyButton, moreButton, reactsDiv);
    }

    private VerticalLayout createSingleReply(Comment comment, Span replied){
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

        String replyValue = inputField.getValue();

        if (replyValue.length() > 28) {
            replyValue = replyValue.replaceAll("(.{28})", "$1\n");
        }

        Span userReply = new Span(replyValue);

        userReply.addClassName("comment-comments");

        String avatarFile = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

        try (FileInputStream fis = new FileInputStream(avatarFile)) {
             byte[] bytes = fis.readAllBytes();

             StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

             avatar.setImageResource(resource);
        } catch (Exception e) {
                e.printStackTrace();
        }

        VerticalLayout layout1 = new VerticalLayout(name, userReply);
        layout1.addClassName("comment-layout1");

        HorizontalLayout layout2 = new HorizontalLayout(avatarDiv, layout1);

        // Save comment to database
        saveReplyToDatabase(comment, replied);

	List<Reply> replies = replyService.getRepliesByCommentId(comment.getId());

	Long replyId = replies.get(replies.size() - 1).getId();

	Reply reply = replyService.getReplyById(replyId);

        Div div = createSingleReplyFooter(reply);
        div.addClassName("comment-div");

        commentLayout.add(layout2, div);

        return commentLayout;
    }

    private void saveReplyToDatabase(Comment comment, Span replied){
    	String reply = inputField.getValue();
        User replier = userService.findCurrentUser();

        replyService.saveReply(replier, reply, comment);

        List<Reply> replies = replyService.getRepliesByCommentId(comment.getId());
        if(replies.size() == 1){
           replied.setText(String.valueOf(replies.size()) + " reply");
        }else{
           replied.setText(String.valueOf(replies.size()) + " replies");
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

                // Save the uploaded file to the specified directory
                String filePath = "/src/main/resources/META-INF/resources/comments_images/" + originalFilename;
                FileOutputStream outputStream = new FileOutputStream(filePath);
                //outputStream.write(bytes);

            } catch (IOException e) {
                Notification.show("Error uploading artwork image", 3000, Notification.Position.TOP_CENTER);
            }
        });

        return upload;
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

    private void createHeader(Comment comment){
    	User user = comment.getUser();

        Span firstName = new Span(formatName(user.getFirstName()) + " replies");
        firstName.addClassName("comment-first-name");

        Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("comment-back-icon");
        backIcon.addClickListener(event -> {
            UI.getCurrent().navigate(CommentSectionView.class, comment.getArtwork().getId());

        });

        addToNavbar(backIcon, firstName);
     }

     public String formatName(String name){
     	if(name.toLowerCase().endsWith("s")){
     	   name += "'";
     	}else{
     	   name += "'s";
     	}

     	return name;
     }
}
