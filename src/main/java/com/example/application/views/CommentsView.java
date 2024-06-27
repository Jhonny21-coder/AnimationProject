package com.example.application.views;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.HasUrlParameter;
import com.example.application.data.*;
import com.example.application.services.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.*;
import java.util.List;
import java.time.LocalDate;

import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.ArrayList;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.UI;

import com.example.application.views.profile.UserProfile;
import com.example.application.views.profile.ViewSpecificArtwork;
import com.vaadin.flow.server.VaadinSession;
import java.util.Set;

@Route("commentsview")
public class CommentsView extends AppLayout implements HasUrlParameter<Long> {

    private final CommentService commentService;
    private final UserServices userService;
    private final ArtworkService artworkService;
    private String submittedText;
    private String originalFilename;

    public CommentsView(CommentService commentService, UserServices userService,
    	ArtworkService artworkService) {
        this.commentService = commentService;
        this.userService = userService;
        this.artworkService = artworkService;

        addClassName("comments-main");
    }

    @Override
    public void setParameter(BeforeEvent event, Long artworkId) {
        List<Comment> comments = commentService.getCommentsByArtworkId(artworkId);

        Artwork artwork = artworkService.getArtworkById(artworkId);

	User user = artwork.getUser();

        displayComments(artworkId, comments, user);
        createHeader(user, artwork);
    }

    public void displayComments(Long artworkId, List<Comment> comments, User user){
	User current = userService.findCurrentUser();

	String email = user.getEmail();

        /*MessageList list = new MessageList();
        list.addClassName("message-list");*/

        Instant newDateTime = Instant.now();
        String newFullName = current.getFullName();
	String newImage = current.getProfileImage();

	VerticalLayout chatLayout = new VerticalLayout();

        for(Comment comment : comments){
            MessageList list = new MessageList();
            list.addClassName("message-list");

	    String userImage = "./register_images/" + comment.getUserImage();

	     MessageListItem message = new MessageListItem(comment.getComments(),
             	comment.getDateTime(), comment.getFullName(), userImage);
	     message.addThemeNames("message");

	     List<MessageListItem> items = new ArrayList<>(list.getItems());
             items.add(message);

             list.setItems(items);

             chatLayout.add(list, createCommentFooter());
	 }

         //Div div = createSendCommentListener(list, newDateTime, newFullName, newImage, email, artworkId);

	 Div footer = createCommentFooter();

         //VerticalLayout chatLayout = new VerticalLayout(list);
         chatLayout.addClassName("chatLayout");

         setContent(chatLayout);

         //addToNavbar(true, div);
     }

     private Div createCommentFooter(){
        Span likeButton = new Span("Like");
        likeButton.addClassName("comment-buttons");

        Span reacts = new Span();
        reacts.addClassName("reacts");

        Span replyButton = new Span("Reply");
        replyButton.addClassName("comment-buttons");
        replyButton.addClickListener(event -> {

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

        Span viewReply = new Span("View 4 replies");
        viewReply.addClassName("comment-view-reply");

        return new Div(likeButton, replyButton, moreButton, reactsDiv, viewReply);
    }

     private Upload createUploadImage(TextField input){
     	Icon cameraIcon = new Icon(VaadinIcon.CAMERA);
        cameraIcon.addClassName("icons");

     	Upload upload = new Upload(new MemoryBuffer());
        // Set the uploadButton as the upload button
        upload.setUploadButton(cameraIcon);
        upload.addClassName("comments-upload");
        upload.setAcceptedFileTypes("image/png");
        upload.addSucceededListener(event -> {
            MemoryBuffer buffer = (MemoryBuffer) upload.getReceiver();

            try {
                InputStream inputStream = buffer.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                originalFilename = event.getFileName();
                input.setValue(event.getFileName());
                StreamResource resource = new StreamResource(originalFilename, () -> new ByteArrayInputStream(bytes));

                // Save the uploaded file to the specified directory
                String filePath = "/data/data/com.termux/files/home/MyVaadinProject/src/main/resources/META-INF/resources/comments_images/" + originalFilename;
                FileOutputStream outputStream = new FileOutputStream(filePath);
                outputStream.write(bytes);

            } catch (IOException e) {
                Notification.show("Error uploading artwork image", 3000, Notification.Position.TOP_CENTER);
            }
        });

        return upload;
     }

     private Div createSendCommentListener(MessageList list, Instant dateTime, String fullName, String image, String email, Long artworkId){
     	TextField input = new TextField();
	input.addClassName("input");
	input.getElement().getStyle().set("--lumo-border-radius", "10px");
        input.setPlaceholder("Add comment...");

     	Icon icon1 = new Icon(VaadinIcon.SMILEY_O);
        Icon icon2 = new Icon(VaadinIcon.SPECIALIST);
        Icon send = new Icon(VaadinIcon.PAPERPLANE);
        send.addClassName("send-icon");
        icon1.addClassName("icons");
        icon2.addClassName("icons");

	Upload upload = createUploadImage(input);

        HorizontalLayout iconsLayout = new HorizontalLayout(icon2, upload, icon1);
        iconsLayout.addClassName("icons-comments");

        // Set the layout as the suffix component of the input field
        input.setSuffixComponent(iconsLayout);

        send.addClickListener(submitEvent -> {
             submittedText = input.getValue();

             input.setValue("");

             // Check if the submitted text length exceeds 28 characters
             if (submittedText.length() > 28) {
                 // Insert newline characters every 28 characters
                 submittedText = submittedText.replaceAll("(.{28})", "$1\n");
             }

             String newImage = "./register_images/" + image;
             // Create a new MessageListItem with the modified text
             MessageListItem newMessage = new MessageListItem(submittedText, dateTime, fullName, newImage);
             newMessage.addThemeNames("message");

             // Get the current list of items
             List<MessageListItem> items = new ArrayList<>(list.getItems());
             // Add the new message to the list
             items.add(newMessage);

             // Update the items in the MessageList
             list.setItems(items);

             commentService.saveComment(email, fullName, dateTime, submittedText, image, artworkId);
         });

         return new Div(input, send);
     }

     private void createHeader(User user, Artwork artwork){
     	String modifiedFullName = user.getFirstName();

        if(modifiedFullName.endsWith("s")){
            modifiedFullName += "'";
        }else{
            modifiedFullName += "'s";
        }

        H1 name = new H1(modifiedFullName + " comments");
        name.addClassName("comment-name");

        Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("back-icon");
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

        addToNavbar(backIcon, name);
     }
}
